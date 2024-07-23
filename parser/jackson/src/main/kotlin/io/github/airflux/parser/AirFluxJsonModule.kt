/*
 * Copyright 2021-2024 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.parser

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import java.util.*

public object AirFluxJsonModule : SimpleModule() {

    init {
        addSerializer(JsValue::class.java, AirFluxSerializer())
        addDeserializer(JsValue::class.java, AirFluxDeserializer())
    }

    private class AirFluxSerializer : JsonSerializer<JsValue>() {

        override fun serialize(value: JsValue, gen: JsonGenerator, provider: SerializerProvider?) {
            when (value) {
                is JsNull -> gen.writeNull()
                is JsString -> gen.writeString(value.get)
                is JsBoolean -> gen.writeBoolean(value.get)
                is JsNumber -> gen.writeNumber(value.get)
                is JsArray -> {
                    gen.writeStartArray()
                    value.forEach { element ->
                        serialize(element, gen, provider)
                    }
                    gen.writeEndArray()
                }

                is JsStruct -> {
                    gen.writeStartObject()
                    value.forEach { (name, element) ->
                        gen.writeFieldName(name)
                        serialize(element, gen, provider)
                    }
                    gen.writeEndObject()
                }
            }
        }
    }

    private class AirFluxDeserializer : JsonDeserializer<JsValue>() {

        override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): JsValue =
            deserialize(jp, ctxt, Stack())

        private tailrec fun deserialize(
            jp: JsonParser,
            context: DeserializationContext,
            parserContext: Stack<DeserializerContext>
        ): JsValue {

            if (jp.currentToken == null) jp.nextToken()
            val tokenId: JsonToken = jp.currentToken

            val maybeValue: JsValue?
            val nextContext: Stack<DeserializerContext>
            when (tokenId) {
                JsonToken.VALUE_TRUE -> {
                    maybeValue = JsBoolean.True
                    nextContext = parserContext
                }

                JsonToken.VALUE_FALSE -> {
                    maybeValue = JsBoolean.False
                    nextContext = parserContext
                }

                JsonToken.VALUE_STRING -> {
                    maybeValue = JsString(jp.text)
                    nextContext = parserContext
                }

                JsonToken.VALUE_NUMBER_INT -> {
                    maybeValue = JsNumber.Integer.valueOrNullOf(jp.text)
                        ?: throw ParsingException("Invalid number value.")
                    nextContext = parserContext
                }

                JsonToken.VALUE_NUMBER_FLOAT -> {
                    maybeValue =
                        JsNumber.Real.valueOrNullOf(jp.text) ?: throw ParsingException("Invalid number value.")
                    nextContext = parserContext
                }

                JsonToken.VALUE_NULL -> {
                    maybeValue = JsNull
                    nextContext = parserContext
                }

                JsonToken.START_ARRAY -> {
                    maybeValue = null
                    nextContext = parserContext.apply {
                        push(DeserializerContext.ReadingList())
                    }
                }

                JsonToken.END_ARRAY -> {
                    val head = parserContext.pop()
                    if (head is DeserializerContext.ReadingList) {
                        maybeValue = JsArray(head.items)
                        nextContext = parserContext
                    } else
                        throw ParsingException("We should have been reading list, something got wrong")
                }

                JsonToken.START_OBJECT -> {
                    maybeValue = null
                    nextContext = parserContext.apply {
                        push(DeserializerContext.ReadingObject())
                    }
                }

                JsonToken.FIELD_NAME -> {
                    val head = parserContext.pop()
                    if (head is DeserializerContext.ReadingObject) {
                        parserContext.push(head.setField(jp.currentName))
                        maybeValue = null
                        nextContext = parserContext
                    } else
                        throw ParsingException("We should be reading map, something got wrong")
                }

                JsonToken.END_OBJECT -> {
                    val head = parserContext.pop()
                    if (head is DeserializerContext.ReadingObject) {
                        maybeValue = JsStruct(head.properties)
                        nextContext = parserContext
                    } else
                        throw ParsingException("We should have been reading an object, something got wrong ($head)")
                }

                JsonToken.NOT_AVAILABLE ->
                    throw ParsingException("We should have been reading an object, something got wrong")

                JsonToken.VALUE_EMBEDDED_OBJECT ->
                    throw ParsingException("We should have been reading an object, something got wrong")
            }

            // Read ahead
            jp.nextToken()

            return if (maybeValue != null && nextContext.isEmpty())
                maybeValue
            else {
                val toPass: Stack<DeserializerContext> = maybeValue?.let { v ->
                    val previous: DeserializerContext = nextContext.pop()
                    val p = previous.addValue(v)
                    nextContext.push(p)
                    nextContext
                } ?: nextContext

                deserialize(jp, context, toPass)
            }
        }

        // This is used when the root object is null, ie when deserialising "null"
        override fun getNullValue(): JsNull = JsNull

        private sealed class DeserializerContext {

            abstract fun addValue(value: JsValue): DeserializerContext

            class ReadingList : DeserializerContext() {
                private val _items: MutableList<JsValue> = mutableListOf()
                val items: List<JsValue>
                    get() = _items

                override fun addValue(value: JsValue): DeserializerContext = this.apply { _items.add(value) }
            }

            class ReadingObject : DeserializerContext() {
                private val _properties: MutableList<Pair<String, JsValue>> = mutableListOf()
                val properties: List<Pair<String, JsValue>>
                    get() = _properties

                fun setField(fieldName: String): KeyRead = KeyRead(fieldName)

                override fun addValue(value: JsValue): DeserializerContext =
                    throw ParsingException("Cannot add a value on an object without a key, malformed JSON object!")

                inner class KeyRead(val fieldName: String) : DeserializerContext() {
                    override fun addValue(value: JsValue): DeserializerContext =
                        this@ReadingObject.apply { _properties.add(fieldName to value) }
                }
            }
        }

        class ParsingException(message: String) : RuntimeException(message)
    }
}
