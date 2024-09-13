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

package io.github.airflux.serialization.core.writer.struct

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.core.writer.nullable
import io.github.airflux.serialization.core.writer.optional
import io.github.airflux.serialization.core.writer.struct.property.JsStructProperty
import io.github.airflux.serialization.core.writer.struct.property.specification.nonNullable
import io.github.airflux.serialization.core.writer.struct.property.specification.nullable
import io.github.airflux.serialization.test.dummy.DummyWriter
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class StructWriterBuildersTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = 42

        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The buildStructWriter function" - {

            "when the property writer is intended for non-nullable value" - {
                val source = NonNullableId(get = PROPERTY_VALUE)
                val propertySpec = nonNullable<OPTS, NonNullableId, Int>(
                    name = PROPERTY_NAME,
                    from = { -> get },
                    writer = DummyWriter.int()
                )
                val writer = buildStructWriter(listOf(JsStructProperty(propertySpec)))

                withData(
                    nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                    RETURN_EMPTY_VALUE to
                        JsStruct(PROPERTY_NAME to JsNumber.valueOf(PROPERTY_VALUE.toString())!!),
                    RETURN_NOTHING to
                        JsStruct(PROPERTY_NAME to JsNumber.valueOf(PROPERTY_VALUE.toString())!!),
                    RETURN_NULL_VALUE to
                        JsStruct(PROPERTY_NAME to JsNumber.valueOf(PROPERTY_VALUE.toString())!!)
                ) { (action, expected) ->
                    val env =
                        JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                    val actual = writer.write(env = env, location = LOCATION, source = source)
                    actual shouldBe expected
                }
            }

            "when the property writer is intended for nullable value" - {

                "when the source contains non-null value" - {
                    val source = NullableId(get = PROPERTY_VALUE)

                    "when the writer for the property writes nullable values as JsNull value" - {
                        val propertyWriter = DummyWriter.int<OPTS>().nullable()
                        val propertySpec = nullable<OPTS, NullableId, Int>(
                            name = PROPERTY_NAME,
                            from = { -> get },
                            writer = propertyWriter
                        )
                        val writer: JsWriter<OPTS, NullableId> =
                            buildStructWriter(listOf(JsStructProperty(propertySpec)))

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsStruct(PROPERTY_NAME to JsNumber.valueOf(PROPERTY_VALUE.toString())!!),
                            RETURN_NOTHING to JsStruct(PROPERTY_NAME to JsNumber.valueOf(PROPERTY_VALUE.toString())!!),
                            RETURN_NULL_VALUE to JsStruct(PROPERTY_NAME to JsNumber.valueOf(PROPERTY_VALUE.toString())!!)
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual = writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }

                    "when the writer for the property writes nullable values as the null" - {
                        val propertyWriter = DummyWriter.int<OPTS>().optional()
                        val propertySpec = nullable<OPTS, NullableId, Int>(
                            name = PROPERTY_NAME,
                            from = { -> get },
                            writer = propertyWriter
                        )
                        val writer: JsWriter<OPTS, NullableId> =
                            buildStructWriter(listOf(JsStructProperty(propertySpec)))

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsStruct(PROPERTY_NAME to JsNumber.valueOf(PROPERTY_VALUE.toString())!!),
                            RETURN_NOTHING to JsStruct(PROPERTY_NAME to JsNumber.valueOf(PROPERTY_VALUE.toString())!!),
                            RETURN_NULL_VALUE to JsStruct(PROPERTY_NAME to JsNumber.valueOf(PROPERTY_VALUE.toString())!!)
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual = writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }
                }

                "when the source contains null value" - {
                    val source = NullableId(get = null)

                    "when the writer for the property writes nullable values as JsNull value" - {
                        val propertyWriter = DummyWriter.int<OPTS>().nullable()
                        val propertySpec = nullable<OPTS, NullableId, Int>(
                            name = PROPERTY_NAME,
                            from = { -> get },
                            writer = propertyWriter
                        )
                        val writer: JsWriter<OPTS, NullableId> =
                            buildStructWriter(listOf(JsStructProperty(propertySpec)))

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsStruct(PROPERTY_NAME to JsNull),
                            RETURN_NOTHING to JsStruct(PROPERTY_NAME to JsNull),
                            RETURN_NULL_VALUE to JsStruct(PROPERTY_NAME to JsNull)
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual = writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }

                    "when the writer for the property writes nullable values as the null" - {
                        val propertyWriter = DummyWriter.int<OPTS>().optional()
                        val propertySpec = nullable<OPTS, NullableId, Int>(
                            name = PROPERTY_NAME,
                            from = { -> get },
                            writer = propertyWriter
                        )
                        val writer: JsWriter<OPTS, NullableId> =
                            buildStructWriter(listOf(JsStructProperty(propertySpec)))

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsStruct(),
                            RETURN_NOTHING to null,
                            RETURN_NULL_VALUE to JsNull
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual = writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }
                }
            }
        }
    }

    internal class OPTS(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
        WriterActionBuilderIfResultIsEmptyOption

    internal class NonNullableId(val get: Int)

    internal class NullableId(val get: Int?)
}
