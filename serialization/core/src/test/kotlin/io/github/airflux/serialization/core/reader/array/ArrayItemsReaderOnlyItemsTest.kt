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

package io.github.airflux.serialization.core.reader.array

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
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

internal class ArrayItemsReaderOnlyItemsTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE = "123"
        private const val SECOND_PHONE_VALUE = "456"
        private const val NUMBER_VALUE = "10"

        private val LOCATION: JsLocation = JsLocation
        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()
        private val reader = arrayItemsReader(StringReader)
    }

    init {

        "The `JsArrayItemsReader` type for only items" - {

            "when the source is empty" - {
                val source = JsArray()

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = env(failFast = true)

                    "then the reader should return the result with an empty list" {
                        val result = reader.read(
                            env = envWithFailFastIsTrue,
                            location = LOCATION,
                            source = source
                        )

                        result.shouldBeSuccess(location = LOCATION, value = emptyList())
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = env(failFast = false)

                    "then the reader should return the result with an empty list" {
                        val result = reader.read(
                            env = envWithFailFastIsFalse,
                            location = LOCATION,
                            source = source
                        )

                        result.shouldBeSuccess(location = LOCATION, value = emptyList())
                    }
                }
            }

            "when the source is not empty" - {

                "when the source does not have any errors" - {
                    val source = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = env(failFast = true)

                        "then the reader should return result" {
                            val result = reader.read(
                                env = envWithFailFastIsTrue,
                                location = LOCATION,
                                source = source
                            )

                            result.shouldBeSuccess(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = env(failFast = false)

                        "then the reader should return result" {
                            val result = reader.read(
                                env = envWithFailFastIsFalse,
                                location = LOCATION,
                                source = source
                            )

                            result.shouldBeSuccess(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when the source has a read error" - {
                    val source = JsArray(JsNumber.valueOf(NUMBER_VALUE)!!, JsBoolean.True)

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = env(failFast = true)

                        "then the reader should return first error" {
                            val result = reader.read(
                                env = envWithFailFastIsTrue,
                                location = LOCATION,
                                source = source
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
                        val envWithFailFastIsFalse = env(failFast = false)

                        "then the reader should return all errors" {
                            val result = reader.read(
                                env = envWithFailFastIsFalse,
                                location = LOCATION,
                                source = source
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

    private fun env(failFast: Boolean): JsReaderEnv<EB, OPTS> =
        JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = failFast)))

    internal class EB : InvalidTypeErrorBuilder,
                        AdditionalItemsErrorBuilder {

        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)

        override fun additionalItemsError(): JsReaderResult.Error = JsonErrors.AdditionalItems
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
