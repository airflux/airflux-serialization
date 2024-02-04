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
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class ArrayPropertyReaderForPrefixItemsOnlyTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE = "123"
        private const val SECOND_PHONE_VALUE = "456"
        private const val THIRD_PHONE_VALUE = "789"

        private val LOCATION: JsLocation = JsLocation
        private val IntReader: JsReader<EB, OPTS, Int> = DummyReader.int()
        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()
        private val BooleanReader: JsReader<EB, OPTS, Boolean> = DummyReader.boolean()
    }

    init {

        "The readArray function for the prefix-items-only readers" - {

            "when parameter 'source' is empty" - {
                val source = JsArray()

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                    "then reader should return result" {
                        val result: JsReaderResult<List<String>> = readArray(
                            env = envWithFailFastIsTrue,
                            location = LOCATION,
                            source = source,
                            prefixItemReaders = listOf(StringReader),
                            errorIfAdditionalItems = true
                        )

                        result shouldBeSuccess success(location = LOCATION, value = emptyList())
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                    "then reader should return result" {
                        val result: JsReaderResult<List<String>> = readArray(
                            env = envWithFailFastIsFalse,
                            location = LOCATION,
                            source = source,
                            prefixItemReaders = listOf(StringReader),
                            errorIfAdditionalItems = true
                        )

                        result shouldBeSuccess success(location = LOCATION, value = emptyList())
                    }
                }
            }

            "when parameter 'source' is not empty" - {
                val source = JsArray(
                    JsString(FIRST_PHONE_VALUE),
                    JsString(SECOND_PHONE_VALUE),
                    JsString(THIRD_PHONE_VALUE)
                )

                "when the number of items is more than the number of readers" - {
                    val readers = listOf(StringReader)

                    "when errorIfAdditionalItems is true" - {
                        val errorIfAdditionalItems = true

                        "when fail-fast is true" - {
                            val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                            "then reader should return first error" {
                                val result: JsReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsTrue,
                                    location = LOCATION,
                                    source = source,
                                    prefixItemReaders = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result shouldBeFailure failure(
                                    location = LOCATION.append(1),
                                    error = JsonErrors.AdditionalItems
                                )
                            }
                        }

                        "when fail-fast is false" - {
                            val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                            "then reader should return all errors" {
                                val result: JsReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsFalse,
                                    location = LOCATION,
                                    source = source,
                                    prefixItemReaders = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result.shouldBeFailure(
                                    JsReaderResult.Failure.Cause(
                                        location = LOCATION.append(1),
                                        error = JsonErrors.AdditionalItems
                                    ),
                                    JsReaderResult.Failure.Cause(
                                        location = LOCATION.append(2),
                                        error = JsonErrors.AdditionalItems
                                    )
                                )
                            }
                        }
                    }

                    "when errorIfAdditionalItems is false" - {
                        val errorIfAdditionalItems = false

                        "when fail-fast is true" - {
                            val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                            "then reader should return result" {
                                val result: JsReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsTrue,
                                    location = LOCATION,
                                    source = source,
                                    prefixItemReaders = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result shouldBeSuccess success(
                                    location = LOCATION,
                                    value = listOf(FIRST_PHONE_VALUE)
                                )
                            }
                        }

                        "when fail-fast is false" - {
                            val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                            "then reader should return result" {
                                val result: JsReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsFalse,
                                    location = LOCATION,
                                    source = source,
                                    prefixItemReaders = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result shouldBeSuccess success(
                                    location = LOCATION,
                                    value = listOf(FIRST_PHONE_VALUE)
                                )
                            }
                        }
                    }
                }

                "when the number of items is equal to the number of readers" - {
                    val errorIfAdditionalItems = true
                    val readers = listOf(StringReader, StringReader, StringReader)

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return result" {
                            val result: JsReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsTrue,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = readers,
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return result" {
                            val result: JsReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsFalse,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = readers,
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when the number of items is less than the number of readers" - {
                    val errorIfAdditionalItems = true
                    val readers = listOf(StringReader, StringReader, StringReader, StringReader)

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return result" {
                            val result: JsReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsTrue,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = readers,
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return result" {
                            val result: JsReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsFalse,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = readers,
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when read was some errors" - {
                    val errorIfAdditionalItems = true

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return first error" {
                            val result = readArray(
                                env = envWithFailFastIsTrue,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = listOf(IntReader, StringReader),
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeFailure failure(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsNumeric.Integer.nameOfType),
                                    actual = JsString.nameOfType
                                )
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return all errors" {
                            val result = readArray(
                                env = envWithFailFastIsFalse,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = listOf(IntReader, StringReader, BooleanReader),
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result.shouldBeFailure(
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(JsNumeric.Integer.nameOfType),
                                        actual = JsString.nameOfType
                                    )
                                ),
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION.append(2),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(JsBoolean.nameOfType),
                                        actual = JsString.nameOfType
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    internal class EB : AdditionalItemsErrorBuilder,
                        InvalidTypeErrorBuilder {
        override fun additionalItemsError(): JsReaderResult.Error = JsonErrors.AdditionalItems

        override fun invalidTypeError(expected: Iterable<String>, actual: String): JsReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
