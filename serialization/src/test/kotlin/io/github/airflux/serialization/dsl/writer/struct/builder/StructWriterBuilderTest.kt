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

package io.github.airflux.serialization.dsl.writer.struct.builder

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.optional
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class StructWriterBuilderTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "name"
        private const val PROPERTY_VALUE = "user"
        private val LOCATION = Location.empty
    }

    init {

        "The StructWriterBuilder type" - {

            "when have some properties for writing to an struct" - {
                val from: (String) -> String = { it }
                val writer: Writer<CTX, String> = structWriter {
                    property(nonNullable(name = PROPERTY_NAME, from = from, writer = DummyWriter { StringNode(it) }))
                }

                "then should return the struct with some properties" {
                    val env = WriterEnv(context = CTX(writerActionIfResultIsEmpty = RETURN_NOTHING))
                    val result = writer.write(env = env, location = LOCATION, value = PROPERTY_VALUE)
                    result shouldBe StructNode(PROPERTY_NAME to StringNode(PROPERTY_VALUE))
                }
            }

            "when no properties for writing to an struct" - {
                val from: (String) -> String? = { null }
                val writer: Writer<CTX, String> = structWriter {
                    property(optional(name = PROPERTY_NAME, from = from, writer = DummyWriter { StringNode(it) }))
                }

                "when the action of the writer was set to return empty value" - {
                    val env = WriterEnv(context = CTX(writerActionIfResultIsEmpty = RETURN_EMPTY_VALUE))

                    "then should return the empty value of the StructNode type" {
                        val result = writer.write(env = env, location = LOCATION, value = PROPERTY_VALUE)
                        result shouldBe StructNode()
                    }
                }

                "when the action of the writer was set to return nothing" - {
                    val env = WriterEnv(context = CTX(writerActionIfResultIsEmpty = RETURN_NOTHING))

                    "then should return the null value" {
                        val result = writer.write(env = env, location = LOCATION, value = PROPERTY_VALUE)
                        result.shouldBeNull()
                    }
                }

                "when the action of the writer was set to return null value" - {
                    val env = WriterEnv(context = CTX(writerActionIfResultIsEmpty = RETURN_NULL_VALUE))

                    "then should return the NullNode value" {
                        val result = writer.write(env = env, location = LOCATION, value = PROPERTY_VALUE)
                        result shouldBe NullNode
                    }
                }
            }
        }
    }

    internal class CTX(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
        WriterActionBuilderIfResultIsEmptyOption
}
