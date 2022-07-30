/*
 * Copyright 2021-2022 Maxim Sambulat.
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
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.ValueNode
import java.util.*

public object AirFluxJsonModule : SimpleModule("AirFlux", Version.unknownVersion()) {

    public class ParsingException(message: String) : RuntimeException(message)

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
            return if (ValueNode::class.java.isAssignableFrom(klass) || klass == NullNode::class.java)
                ValueNodeDeserializer(klass)
            else
                null
        }
    }

    private class ValueNodeDeserializer(val klass: Class<*>) : JsonDeserializer<Any>() {

        override fun isCachable(): Boolean = true

        override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): ValueNode =
            deserialize(jp, ctxt, Stack())
                .also { value ->
                    if (!klass.isAssignableFrom(value::class.java))
                        ctxt.handleUnexpectedToken(klass, jp)
                }

        private tailrec fun deserialize(
            jp: JsonParser,
            context: DeserializationContext,
            parserContext: Stack<DeserializerContext>
        ): ValueNode {

            if (jp.currentToken == null) jp.nextToken()
            val tokenId: JsonToken = jp.currentToken

            val maybeValue: ValueNode?
            val nextContext: Stack<DeserializerContext>
            when (tokenId) {
                JsonToken.VALUE_TRUE -> {
                    maybeValue = BooleanNode.True
                    nextContext = parserContext
                }
                JsonToken.VALUE_FALSE -> {
                    maybeValue = BooleanNode.False
                    nextContext = parserContext
                }
                JsonToken.VALUE_STRING -> {
                    maybeValue = StringNode(jp.text)
                    nextContext = parserContext
                }
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.VALUE_NUMBER_FLOAT -> {
                    maybeValue = NumberNode.valueOf(jp.text) ?: throw ParsingException("Invalid number value.")
                    nextContext = parserContext
                }
                JsonToken.VALUE_NULL -> {
                    maybeValue = NullNode
                    nextContext = parserContext
                }
                JsonToken.START_ARRAY -> {
                    maybeValue = null
                    nextContext = parserContext.apply {
                        push(DeserializerContext.ReadingList(mutableListOf()))
                    }
                }
                JsonToken.END_ARRAY -> {
                    val head = parserContext.pop()
                    if (head is DeserializerContext.ReadingList) {
                        maybeValue = ArrayNode(head.values)
                        nextContext = parserContext
                    } else
                        throw ParsingException("We should have been reading list, something got wrong")
                }
                JsonToken.START_OBJECT -> {
                    maybeValue = null
                    nextContext = parserContext.apply {
                        push(DeserializerContext.ReadingObject(LinkedList()))
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
                        maybeValue = StructNode(head.values.toMap())
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
                    val previous = nextContext.pop()
                    val p = previous.addValue(v)
                    nextContext.push(p)
                    nextContext
                } ?: nextContext

                deserialize(jp, context, toPass)
            }
        }

        // This is used when the root object is null, ie when deserialising "null"
        override fun getNullValue(): NullNode = NullNode

        private sealed class DeserializerContext {
            abstract fun addValue(value: ValueNode): DeserializerContext

            data class ReadingList(val values: MutableList<ValueNode>) : DeserializerContext() {
                override fun addValue(value: ValueNode): DeserializerContext = ReadingList(values.apply { add(value) })
            }

            data class KeyRead(val fieldName: String, val values: MutableList<Pair<String, ValueNode>>) :
                DeserializerContext() {
                override fun addValue(value: ValueNode): DeserializerContext =
                    ReadingObject(values.apply { add(fieldName to value) })
            }

            class ReadingObject(val values: MutableList<Pair<String, ValueNode>>) : DeserializerContext() {
                fun setField(fieldName: String): KeyRead = KeyRead(fieldName, values)
                override fun addValue(value: ValueNode): DeserializerContext =
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

    private class JsValueSerializer : JsonSerializer<ValueNode>() {
        override fun serialize(value: ValueNode, gen: JsonGenerator, provider: SerializerProvider) {
            when (value) {
                is NullNode -> gen.writeNull()
                is StringNode -> gen.writeString(value.get)
                is BooleanNode -> gen.writeBoolean(value.get)
                is NumberNode -> gen.writeNumber(value.get)
                is ArrayNode<*> -> {
                    gen.writeStartArray()
                    value.forEach { element ->
                        serialize(element, gen, provider)
                    }
                    gen.writeEndArray()
                }
                is StructNode -> {
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
