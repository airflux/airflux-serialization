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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.array.arrayWriter
import io.github.airflux.serialization.dsl.writer.array.item.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.array.item.specification.optional
import io.github.airflux.serialization.dsl.writer.array.items
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ArrayWriterBuilderTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM = "item-1"
        private const val SECOND_ITEM = "item-2"

        private val ENV = WriterEnv(context = CTX(writerActionIfResultIsEmpty = RETURN_EMPTY_VALUE))
        private val LOCATION = Location.empty
    }

    init {

        "The ArrayWriterBuilder type" - {

            "when have some non-nullable items for writing to an array" - {
                val writer: Writer<CTX, Iterable<String>> = arrayWriter {
                    items(nonNullable(writer = DummyWriter { StringNode(it) }))
                }
                val value = listOf(FIRST_ITEM, SECOND_ITEM)
                val result = writer.write(env = ENV, location = LOCATION, value = value)

                "then should return the array with items" {
                    result as ArrayNode<*>
                    result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                }
            }

            "when have some optional items for writing to an array" - {
                val writer: Writer<CTX, Iterable<String?>> = arrayWriter {
                    items(optional(writer = DummyWriter { StringNode(it) }))
                }
                val value = listOf(null, FIRST_ITEM, null, SECOND_ITEM, null)
                val result = writer.write(env = ENV, location = LOCATION, value = value)

                "then should return the array with items" {
                    result as ArrayNode<*>
                    result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                }
            }

            "when no items for writing to an array" - {
                val value = emptyList<String>()
                val writer: Writer<CTX, Iterable<String>> = arrayWriter {
                    items(nonNullable(writer = DummyWriter { StringNode(it) }))
                }

                "when the action of the writer was set to return empty value" - {
                    val env = WriterEnv(context = CTX(writerActionIfResultIsEmpty = RETURN_EMPTY_VALUE))

                    "then should return the empty value of the ArrayNode type" {
                        val result = writer.write(env = env, location = LOCATION, value = value)
                        result shouldBe ArrayNode<ValueNode>()
                    }
                }

                "when the action of the writer was set to return nothing" - {
                    val env = WriterEnv(context = CTX(writerActionIfResultIsEmpty = RETURN_NOTHING))

                    "then should return the null value" {
                        val result = writer.write(env = env, location = LOCATION, value = value)
                        result.shouldBeNull()
                    }
                }

                "when the action of the writer was set to return null value" - {
                    val env = WriterEnv(context = CTX(writerActionIfResultIsEmpty = RETURN_NULL_VALUE))

                    "then should return the NullNode value" {
                        val result = writer.write(env = env, location = LOCATION, value = value)
                        result shouldBe NullNode
                    }
                }
            }
        }
    }

    internal class CTX(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
        WriterActionBuilderIfResultIsEmptyOption
}
