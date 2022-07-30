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

package io.github.airflux.serialization.dsl.writer.array.builder

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.nullable
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.optional
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ArrayWriterBuilderTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM = "item-1"
        private const val SECOND_ITEM = "item-2"

        private val CONTEXT = WriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The ArrayWriterBuilder type" - {

            "when have some non-nullable items for writing to an array" - {
                val writer = arrayWriter<String> {
                    items(nonNullable(writer = DummyWriter { StringNode(it) }))
                }
                val value = listOf(FIRST_ITEM, SECOND_ITEM)
                val result = writer.write(context = CONTEXT, location = LOCATION, value = value)

                "then should return the array with items" {
                    result as ArrayNode<*>
                    result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                }
            }

            "when have some optional items for writing to an array" - {
                val writer = arrayWriter<String?> {
                    items(optional(writer = DummyWriter { StringNode(it) }))
                }
                val value = listOf(null, FIRST_ITEM, null, SECOND_ITEM, null)
                val result = writer.write(context = CONTEXT, location = LOCATION, value = value)

                "then should return the array with items" {
                    result as ArrayNode<*>
                    result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                }
            }

            "when have some nullable items for writing to an array" - {
                val writer = arrayWriter<String?> {
                    items(nullable(writer = DummyWriter { StringNode(it) }))
                }
                val value = listOf(null, FIRST_ITEM, null, SECOND_ITEM, null)
                val result = writer.write(context = CONTEXT, location = LOCATION, value = value)

                "then should return the array with items" {
                    result as ArrayNode<*>
                    result shouldBe ArrayNode(
                        NullNode,
                        StringNode(FIRST_ITEM),
                        NullNode,
                        StringNode(SECOND_ITEM),
                        NullNode
                    )
                }
            }

            "when no items for writing to an array" - {
                val value = emptyList<String>()

                "when the action of the writer was not set" - {
                    val writer = arrayWriter<String> {
                        items(nonNullable(writer = DummyWriter { StringNode(it) }))
                    }

                    "then should return the empty value of the ArrayNode type" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = value)
                        result shouldBe ArrayNode<ValueNode>()
                    }
                }

                "when the action of the writer was set to return empty value" - {
                    val writer = arrayWriter<String> {
                        actionIfEmpty = returnEmptyValue()
                        items(nonNullable(writer = DummyWriter { StringNode(it) }))
                    }

                    "then should return the empty value of the ArrayNode type" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = value)
                        result shouldBe ArrayNode<ValueNode>()
                    }
                }

                "when the action of the writer was set to return nothing" - {
                    val writer = arrayWriter<String> {
                        actionIfEmpty = returnNothing()
                        items(nonNullable(writer = DummyWriter { StringNode(it) }))
                    }

                    "then should return the null value" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = value)
                        result.shouldBeNull()
                    }
                }

                "when the action of the writer was set to return null value" - {
                    val writer = arrayWriter<String> {
                        actionIfEmpty = returnNullValue()
                        items(nonNullable(writer = DummyWriter { StringNode(it) }))
                    }

                    "then should return the NullNode value" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = value)
                        result shouldBe NullNode
                    }
                }
            }
        }
    }
}
