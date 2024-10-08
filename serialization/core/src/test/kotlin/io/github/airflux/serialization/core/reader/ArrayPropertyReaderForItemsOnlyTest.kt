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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.kotest.assertions.cause
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.core.spec.style.FreeSpec

internal class ArrayPropertyReaderForItemsOnlyTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE = "123"
        private const val SECOND_PHONE_VALUE = "456"
        private const val NUMBER_VALUE = "10"

        private val LOCATION: JsLocation = JsLocation
        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()
    }

    init {

        "The readArray function for the items-only reader" - {

            "when parameter 'source' is empty" - {
                val source = JsArray()

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue =
                        JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = true)))

                    "then reader should return result" {
                        val result: JsReaderResult<List<String>> = source.readItems(
                            env = envWithFailFastIsTrue,
                            location = LOCATION,
                            itemsReader = StringReader
                        )

                        result.shouldBeSuccess(location = LOCATION, value = emptyList())
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse =
                        JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = false)))

                    "then reader should return result" {
                        val result: JsReaderResult<List<String>> = source.readItems(
                            env = envWithFailFastIsFalse,
                            location = LOCATION,
                            itemsReader = StringReader
                        )

                        result.shouldBeSuccess(location = LOCATION, value = emptyList())
                    }
                }
            }

            "when parameter 'source' is not empty" - {

                "when read was no errors" - {
                    val source = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue =
                            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = true)))

                        "then reader should return result" {
                            val result: JsReaderResult<List<String>> = source.readItems(
                                env = envWithFailFastIsTrue,
                                location = LOCATION,
                                itemsReader = StringReader
                            )

                            result.shouldBeSuccess(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse =
                            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = false)))

                        "then reader should return result" {
                            val result: JsReaderResult<List<String>> = source.readItems(
                                env = envWithFailFastIsFalse,
                                location = LOCATION,
                                itemsReader = StringReader
                            )

                            result.shouldBeSuccess(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when read was some errors" - {
                    val source = JsArray(JsNumber.valueOf(NUMBER_VALUE)!!, JsBoolean.True)

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue =
                            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = true)))

                        "then the validator should return first error" {
                            val result: JsReaderResult<List<String>> = source.readItems(
                                env = envWithFailFastIsTrue,
                                location = LOCATION,
                                itemsReader = StringReader
                            )

                            result.shouldBeFailure(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse =
                            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = false)))

                        "then the validator should return all errors" {
                            val result: JsReaderResult<List<String>> = source.readItems(
                                env = envWithFailFastIsFalse,
                                location = LOCATION,
                                itemsReader = StringReader
                            )

                            result.shouldBeFailure(
                                cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.NUMBER
                                    )
                                ),
                                cause(
                                    location = LOCATION.append(1),
                                    error = JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.BOOLEAN
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {

        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
