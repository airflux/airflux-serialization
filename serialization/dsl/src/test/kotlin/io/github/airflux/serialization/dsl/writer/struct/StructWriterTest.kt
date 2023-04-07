/*
 * Copyright 2021-2023 Maxim Sambulat.
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

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.nullable
import io.github.airflux.serialization.core.writer.optional
import io.github.airflux.serialization.dsl.common.DummyWriter
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.dsl.writer.struct.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.property.specification.nullable
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class StructWriterTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = 42

        private val CONTEXT = Unit
        private val LOCATION = Location.empty
    }

    init {

        "The StructWriter type" - {

            "when a property is non-nullable type" - {
                val source = ID(id = ID_PROPERTY_VALUE)

                val writer: Writer<OPTS, Unit, ID> = structWriter {
                    property(nonNullable(name = ID_PROPERTY_NAME, from = { -> id }, writer = DummyWriter.intWriter()))
                }

                "when the action of the writer was set to return empty value" - {
                    val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_EMPTY_VALUE))

                    "then should return a struct with property" {
                        val result =
                            writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                        result shouldBe StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE)
                        )
                    }
                }

                "when the action of the writer was set to return nothing" - {
                    val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NOTHING))

                    "then should return a struct with property" {
                        val result =
                            writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                        result shouldBe StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE)
                        )
                    }
                }

                "when the action of the writer was set to return null value" - {
                    val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NULL_VALUE))

                    "then should return a struct with property" {
                        val result =
                            writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                        result shouldBe StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE)
                        )
                    }
                }
            }

            "when a property is nullable type" - {
                val source = NullableID(get = null)

                "when a writer of property is nullable" - {
                    val writer: Writer<OPTS, Unit, NullableID> = structWriter {
                        property(
                            nullable(
                                name = ID_PROPERTY_NAME,
                                from = { -> get },
                                writer = DummyWriter.intWriter<OPTS, Unit>().nullable()
                            )
                        )
                    }

                    "when the action of the writer was set to return empty value" - {
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_EMPTY_VALUE))

                        "then should return a struct with property" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe StructNode(ID_PROPERTY_NAME to NullNode)
                        }
                    }

                    "when the action of the writer was set to return nothing" - {
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NOTHING))

                        "then should return a struct with property" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe StructNode(ID_PROPERTY_NAME to NullNode)
                        }
                    }

                    "when the action of the writer was set to return null value" - {
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NULL_VALUE))

                        "then should return a struct with property" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe StructNode(ID_PROPERTY_NAME to NullNode)
                        }
                    }
                }

                "when a writer of property is optional" - {
                    val writer: Writer<OPTS, Unit, NullableID> = structWriter {
                        property(
                            nullable(
                                name = ID_PROPERTY_NAME,
                                from = { -> get },
                                writer = DummyWriter.intWriter<OPTS, Unit>().optional()
                            )
                        )
                    }

                    "when the action of the writer was set to return empty value" - {
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_EMPTY_VALUE))

                        "then should return an empty struct" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe StructNode()
                        }
                    }

                    "when the action of the writer was set to return nothing" - {
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NOTHING))

                        "then should return a null value" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result.shouldBeNull()
                        }
                    }

                    "when the action of the writer was set to return null value" - {
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NULL_VALUE))

                        "then should return a NullNode value" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe NullNode
                        }
                    }

                    /*"when the source contains all properties" - {
                        val source = NullableDTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NOTHING))
                        val result = writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)

                        "then should return a struct with all properties" {
                            result shouldBe StructNode(
                                ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE),
                                NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE)
                            )
                        }
                    }

                    "when the source does not contain any properties" - {
                        val source = NullableDTO(id = null)

                        "when the action of the writer was set to return empty value" - {
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_EMPTY_VALUE))

                            "then return a struct with all properties with NullNode values" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe StructNode(
                                    ID_PROPERTY_NAME to NullNode,
                                    NAME_PROPERTY_NAME to NullNode
                                )
                            }
                        }

                        "when the action of the writer was set to return nothing" - {
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NOTHING))

                            "then return a struct with all properties with NullNode values" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)

                                result shouldBe StructNode(
                                    ID_PROPERTY_NAME to NullNode,
                                    NAME_PROPERTY_NAME to NullNode
                                )
                            }
                        }

                        "when the action of the writer was set to return null value" - {
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = RETURN_NULL_VALUE))

                            "then return a struct with all properties with NullNode values" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)

                                result shouldBe StructNode(
                                    ID_PROPERTY_NAME to NullNode,
                                    NAME_PROPERTY_NAME to NullNode
                                )
                            }
                        }
                    }*/
                }
            }
        }
    }

    internal class OPTS(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
        WriterActionBuilderIfResultIsEmptyOption

    internal class ID(val id: Int)

    internal class NullableID(val get: Int?)
}
