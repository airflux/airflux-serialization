/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.Serializers
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import java.util.*

object AirFluxJsonModule : SimpleModule("AirFlux", Version.unknownVersion()) {

    class ParsingException(message: String) : RuntimeException(message)

    override fun setupModule(context: SetupContext) {
        context.addDeserializers(AirFluxDeserializers())
        context.addSerializers(AirFluxSerializers())
    }

    private class AirFluxDeserializers : Deserializers.Base() {
        override fun findBeanDeserializer(
            javaType: JavaType,
            config: DeserializationConfig,
            beanDesc: BeanDescription
        ): JsonDeserializer<*>? {
            val klass = javaType.rawClass
            return if (JsValue::class.java.isAssignableFrom(klass) || klass == JsNull::class.java)
                JsValueDeserializer(klass)
            else
                null
        }
    }

    private class JsValueDeserializer(val klass: Class<*>) : JsonDeserializer<Any>() {

        override fun isCachable(): Boolean = true

        override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): JsValue =
            deserialize(jp, ctxt, Stack())
                .also { value ->
                    if (!klass.isAssignableFrom(value::class.java))
                        ctxt.handleUnexpectedToken(klass, jp)
                }

        private tailrec fun deserialize(
            jp: JsonParser,
            ctxt: DeserializationContext,
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
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.VALUE_NUMBER_FLOAT -> {
                    maybeValue = JsNumber.valueOf(jp.text) ?: throw ParsingException("Invalid number value.")
                    nextContext = parserContext
                }
                JsonToken.VALUE_NULL -> {
                    maybeValue = JsNull
                    nextContext = parserContext
                }
                JsonToken.START_ARRAY -> {
                    maybeValue = null
                    nextContext = parserContext.apply {
                        push(DeserializerContext.ReadingList(mutableListOf()))
                    }
                }
                JsonToken.END_ARRAY -> {
                    when (val head = parserContext.pop()) {
                        is DeserializerContext.ReadingList -> {
                            maybeValue = JsArray(head.values)
                            nextContext = parserContext
                        }
                        else -> throw ParsingException("We should have been reading list, something got wrong")
                    }
                }
                JsonToken.START_OBJECT -> {
                    maybeValue = null
                    nextContext = parserContext.apply {
                        push(DeserializerContext.ReadingObject(LinkedList()))
                    }
                }
                JsonToken.FIELD_NAME -> {
                    when (val head = parserContext.pop()) {
                        is DeserializerContext.ReadingObject -> {
                            parserContext.push(head.setField(jp.currentName))
                            maybeValue = null
                            nextContext = parserContext
                        }
                        else -> throw ParsingException("We should be reading map, something got wrong")
                    }
                }
                JsonToken.END_OBJECT -> {
                    when (val head = parserContext.pop()) {
                        is DeserializerContext.ReadingObject -> {
                            maybeValue = JsObject(head.values.toMap())
                            nextContext = parserContext
                        }
                        else ->
                            throw ParsingException("We should have been reading an object, something got wrong ($head)")
                    }
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
                    val previous = nextContext.pop()
                    val p = previous.addValue(v)
                    nextContext.push(p)
                    nextContext
                } ?: nextContext

                deserialize(jp, ctxt, toPass)
            }
        }

        // This is used when the root object is null, ie when deserialising "null"
        override fun getNullValue() = JsNull

        private sealed class DeserializerContext {
            abstract fun addValue(value: JsValue): DeserializerContext

            data class ReadingList(val values: MutableList<JsValue>) : DeserializerContext() {
                override fun addValue(value: JsValue): DeserializerContext = ReadingList(values.apply { add(value) })
            }

            data class KeyRead(val fieldName: String, val values: MutableList<Pair<String, JsValue>>) :
                DeserializerContext() {
                override fun addValue(value: JsValue): DeserializerContext =
                    ReadingObject(values.apply { add(fieldName to value) })
            }

            data class ReadingObject(val values: MutableList<Pair<String, JsValue>>) : DeserializerContext() {
                fun setField(fieldName: String): KeyRead = KeyRead(fieldName, values)
                override fun addValue(value: JsValue): DeserializerContext =
                    throw ParsingException("Cannot add a value on an object without a key, malformed JSON object!")
            }
        }
    }

    private class AirFluxSerializers : Serializers.Base() {
        override fun findSerializer(
            config: SerializationConfig,
            type: JavaType,
            beanDesc: BeanDescription
        ): JsonSerializer<*> = JsValueSerializer()
    }

    private class JsValueSerializer : JsonSerializer<JsValue>() {
        override fun serialize(value: JsValue, gen: JsonGenerator, provider: SerializerProvider) {
            when (value) {
                is JsNull -> gen.writeNull()
                is JsString -> gen.writeString(value.get)
                is JsBoolean -> gen.writeBoolean(value.get)
                is JsNumber -> gen.writeNumber(value.get)
                is JsArray<*> -> {
                    gen.writeStartArray()
                    value.forEach { element ->
                        serialize(element, gen, provider)
                    }
                    gen.writeEndArray()
                }
                is JsObject -> {
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
}
