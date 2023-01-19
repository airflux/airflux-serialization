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

package io.github.airflux.serialization.dsl.writer.struct

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.struct.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.property.specification.nullable
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class StructWriterTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = 42
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "user"

        private val CONTEXT = Unit
        private val LOCATION = Location.empty
    }

    init {

        "The StructWriter type" - {

            "when a writer was created with non-nullable property" - {
                val writer: Writer<OPTS, Unit, DTO> = structWriter {
                    property(nonNullable(name = ID_PROPERTY_NAME, from = DTO::id, writer = DummyWriter.intWriter()))
                    property(nullable(name = NAME_PROPERTY_NAME, from = DTO::name, writer = DummyWriter.stringWriter()))
                }

                "when the source contains all properties" - {
                    val source = DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                    val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NOTHING))
                    val result = writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)

                    "then should return a struct with all properties" {
                        result shouldBe StructNode(
                            ID_PROPERTY_NAME to NumericNode.Integer.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE)
                        )
                    }
                }

                "when the source contains only non-nullable properties" - {
                    val source = DTO(id = ID_PROPERTY_VALUE, name = null)
                    val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NOTHING))
                    val result = writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)

                    "then should return a struct with only non-nullable properties" {
                        result shouldBe StructNode(
                            ID_PROPERTY_NAME to NumericNode.Integer.valueOf(ID_PROPERTY_VALUE)
                        )
                    }
                }
            }

            "when a writer was created with all nullable properties" - {
                val writer: Writer<OPTS, Unit, NullableDTO> = structWriter {
                    property(
                        nullable(
                            name = ID_PROPERTY_NAME,
                            from = NullableDTO::id,
                            writer = DummyWriter.intWriter()
                        )
                    )
                    property(
                        nullable(
                            name = NAME_PROPERTY_NAME,
                            from = NullableDTO::name,
                            writer = DummyWriter.stringWriter()
                        )
                    )
                }

                "when the source contains all properties" - {
                    val source = NullableDTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                    val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NOTHING))
                    val result = writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)

                    "then should return a struct with all properties" {
                        result shouldBe StructNode(
                            ID_PROPERTY_NAME to NumericNode.Integer.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE)
                        )
                    }
                }

                "when the source does not contain any properties" - {
                    val source = NullableDTO(id = null, name = null)

                    "when the action of the writer was set to return empty value" - {
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_EMPTY_VALUE))

                        "then should return the empty value of the StructNode type" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe StructNode()
                        }
                    }

                    "when the action of the writer was set to return nothing" - {
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NOTHING))

                        "then should return the null value" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result.shouldBeNull()
                        }
                    }

                    "when the action of the writer was set to return null value" - {
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NULL_VALUE))

                        "then should return the NullNode value" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe NullNode
                        }
                    }
                }
            }
        }
    }

    internal class OPTS(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
        WriterActionBuilderIfResultIsEmptyOption

    internal class DTO(val id: Int, val name: String?)

    internal class NullableDTO(val id: Int?, val name: String?)
}
