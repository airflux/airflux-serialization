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

package io.github.airflux.serialization.core.writer.array

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.github.airflux.serialization.core.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.core.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.github.airflux.serialization.core.writer.nullable
import io.github.airflux.serialization.core.writer.optional
import io.github.airflux.serialization.test.dummy.DummyWriter
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class ArrayWriterBuildersTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM = "item-1"
        private const val SECOND_ITEM = "item-2"

        private val LOCATION: JsLocation = JsLocation

        private val StringWriter = DummyWriter.string<OPTS>()
    }

    init {

        "The buildArrayWriter function" - {

            "when the item writer is intended for non-nullable values" - {
                val writer = buildArrayWriter(itemsWriter = StringWriter)

                "when the source contains items" - {
                    val source = listOf(FIRST_ITEM, SECOND_ITEM)

                    withData(
                        nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                        RETURN_EMPTY_VALUE to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM)),
                        RETURN_NOTHING to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM)),
                        RETURN_NULL_VALUE to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))
                    ) { (action, expected) ->
                        val env =
                            JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                        val actual =
                            writer.write(env = env, location = LOCATION, source = source)
                        actual shouldBe expected
                    }
                }

                "when the source does not contain any items" - {
                    val source = emptyList<String>()

                    withData(
                        nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                        RETURN_EMPTY_VALUE to JsArray(),
                        RETURN_NOTHING to null,
                        RETURN_NULL_VALUE to JsNull
                    ) { (action, expected) ->
                        val env =
                            JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                        val actual =
                            writer.write(env = env, location = LOCATION, source = source)
                        actual shouldBe expected
                    }
                }
            }

            "when the item writer is intended for nullable values" - {

                "when the writer writes nullable values as JsNull value" - {
                    val writer = buildArrayWriter(itemsWriter = StringWriter.nullable())

                    "when the source contains only non-nullable items" - {
                        val source = listOf(FIRST_ITEM, SECOND_ITEM)

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM)),
                            RETURN_NOTHING to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM)),
                            RETURN_NULL_VALUE to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual =
                                writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }

                    "when the source contains some nullable items" - {
                        val source = listOf(null, FIRST_ITEM, null, SECOND_ITEM, null)

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to
                                JsArray(JsNull, JsString(FIRST_ITEM), JsNull, JsString(SECOND_ITEM), JsNull),
                            RETURN_NOTHING to
                                JsArray(JsNull, JsString(FIRST_ITEM), JsNull, JsString(SECOND_ITEM), JsNull),
                            RETURN_NULL_VALUE to
                                JsArray(JsNull, JsString(FIRST_ITEM), JsNull, JsString(SECOND_ITEM), JsNull)
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual =
                                writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }

                    "when the source contains all nullable items" - {
                        val source = listOf(null, null, null)

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsArray(JsNull, JsNull, JsNull),
                            RETURN_NOTHING to JsArray(JsNull, JsNull, JsNull),
                            RETURN_NULL_VALUE to JsArray(JsNull, JsNull, JsNull)
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual =
                                writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }

                    "when the source does not contain any items" - {
                        val source = emptyList<String>()

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsArray(),
                            RETURN_NOTHING to null,
                            RETURN_NULL_VALUE to JsNull
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual =
                                writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }
                }

                "when the writer writes nullable values as the null" - {
                    val writer = buildArrayWriter(itemsWriter = StringWriter.optional())

                    "when the source contains only non-nullable items" - {
                        val source = listOf(FIRST_ITEM, SECOND_ITEM)

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM)),
                            RETURN_NOTHING to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM)),
                            RETURN_NULL_VALUE to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual =
                                writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }

                    "when the source contains some nullable items" - {
                        val source = listOf(null, FIRST_ITEM, null, SECOND_ITEM, null)

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM)),
                            RETURN_NOTHING to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM)),
                            RETURN_NULL_VALUE to JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual =
                                writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }

                    "when the source contains all nullable items" - {
                        val source = listOf(null, null, null)

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsArray(),
                            RETURN_NOTHING to null,
                            RETURN_NULL_VALUE to JsNull
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual =
                                writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }

                    "when the source does not contain any items" - {
                        val source = emptyList<String>()

                        withData(
                            nameFn = { (action, _) -> "the action of the writer for an empty result is set to $action" },
                            RETURN_EMPTY_VALUE to JsArray(),
                            RETURN_NOTHING to null,
                            RETURN_NULL_VALUE to JsNull
                        ) { (action, expected) ->
                            val env =
                                JsWriterEnv(config = JsWriterEnv.Config(options = OPTS(writerActionIfResultIsEmpty = action)))
                            val actual =
                                writer.write(env = env, location = LOCATION, source = source)
                            actual shouldBe expected
                        }
                    }
                }
            }
        }
    }

    internal class OPTS(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
        WriterActionBuilderIfResultIsEmptyOption
}
