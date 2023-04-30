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

package io.github.airflux.serialization.core.reader.array

import io.github.airflux.serialization.core.common.DummyReader
import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeFailure
import io.github.airflux.serialization.core.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumeric
import io.github.airflux.serialization.core.value.JsString
import io.kotest.core.spec.style.FreeSpec
import kotlin.reflect.KClass

internal class ArrayPropertyReaderForPrefixItemsOnlyTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE = "123"
        private const val SECOND_PHONE_VALUE = "456"
        private const val THIRD_PHONE_VALUE = "789"

        private val CONTEXT = Unit
        private val LOCATION = JsLocation
        private val IntReader: JsReader<EB, OPTS, Unit, Int> = DummyReader.int()
        private val StringReader: JsReader<EB, OPTS, Unit, String> = DummyReader.string()
        private val BooleanReader: JsReader<EB, OPTS, Unit, Boolean> = DummyReader.boolean()
    }

    init {

        "The readArray function for the prefix-items-only readers" - {

            "when parameter 'source' is empty" - {
                val source = JsArray()

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                    "then reader should return result" {
                        val result: ReadingResult<List<String>> = readArray(
                            env = envWithFailFastIsTrue,
                            context = CONTEXT,
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
                        val result: ReadingResult<List<String>> = readArray(
                            env = envWithFailFastIsFalse,
                            context = CONTEXT,
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
                                val result: ReadingResult<List<String>> = readArray(
                                    env = envWithFailFastIsTrue,
                                    context = CONTEXT,
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
                                val result: ReadingResult<List<String>> = readArray(
                                    env = envWithFailFastIsFalse,
                                    context = CONTEXT,
                                    location = LOCATION,
                                    source = source,
                                    prefixItemReaders = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result.shouldBeFailure(
                                    ReadingResult.Failure.Cause(
                                        location = LOCATION.append(1),
                                        error = JsonErrors.AdditionalItems
                                    ),
                                    ReadingResult.Failure.Cause(
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
                                val result: ReadingResult<List<String>> = readArray(
                                    env = envWithFailFastIsTrue,
                                    context = CONTEXT,
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
                                val result: ReadingResult<List<String>> = readArray(
                                    env = envWithFailFastIsFalse,
                                    context = CONTEXT,
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
                            val result: ReadingResult<List<String>> = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
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
                            val result: ReadingResult<List<String>> = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
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
                            val result: ReadingResult<List<String>> = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
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
                            val result: ReadingResult<List<String>> = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
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
                                context = CONTEXT,
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
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = listOf(IntReader, StringReader, BooleanReader),
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result.shouldBeFailure(
                                ReadingResult.Failure.Cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(JsNumeric.Integer.nameOfType),
                                        actual = JsString.nameOfType
                                    )
                                ),
                                ReadingResult.Failure.Cause(
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
        InvalidTypeErrorBuilder,
        ValueCastErrorBuilder {
        override fun additionalItemsError(): ReadingResult.Error = JsonErrors.AdditionalItems

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected, actual)

        override fun valueCastError(value: String, target: KClass<*>): ReadingResult.Error =
            JsonErrors.ValueCast(value, target)
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
