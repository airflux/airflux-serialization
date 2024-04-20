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

package io.github.airflux.serialization.dsl.reader.array

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
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
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class JsArrayReaderPrefixItemsAndItemsTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM = "first"
        private const val SECOND_ITEM = "second"
        private const val THIRD_ITEM = "third"

        private val LOCATION: JsLocation = JsLocation

        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()
        private val BooleanReader: JsReader<EB, OPTS, Boolean> = DummyReader.boolean()
    }

    init {

        "The JsArrayReader type" - {

            "when a reader was created for prefixItems and items" - {
                val reader: JsReader<EB, OPTS, List<Any>> = arrayReader {
                    returns(prefixItems = listOf(StringReader, StringReader), items = BooleanReader)
                }

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                    "when source is not the array type" - {
                        val source = JsString("")

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsArray.nameOfType),
                                    actual = JsString.nameOfType
                                )
                            )
                        }
                    }

                    "when the number of items is less than the number of elements in prefixItems" - {
                        val source = JsArray(JsString(FIRST_ITEM))

                        "then should return all elements read" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeSuccess success(location = LOCATION, value = listOf(FIRST_ITEM))
                        }
                    }

                    "when the number of items is equal to the number of elements in prefixItems" - {
                        val source =
                            JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))

                        "then should return all elements read" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = listOf(FIRST_ITEM, SECOND_ITEM)
                            )
                        }
                    }

                    "when the number of items is more than the number of elements in prefixItems" - {

                        "when additional items are of valid type" - {
                            val source = JsArray(
                                JsString(FIRST_ITEM),
                                JsString(SECOND_ITEM),
                                JsBoolean.valueOf(true)
                            )

                            "then should return all items read" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result shouldBeSuccess success(
                                    location = LOCATION,
                                    value = listOf(FIRST_ITEM, SECOND_ITEM, true)
                                )
                            }
                        }

                        "when additional items are of invalid type" - {
                            val source = JsArray(
                                JsString(FIRST_ITEM),
                                JsString(SECOND_ITEM),
                                JsString(THIRD_ITEM),
                                JsNumeric.valueOf(10)
                            )

                            "then should return an error" {
                                val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                                result shouldBeFailure failure(
                                    location = LOCATION.append(2),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(JsBoolean.nameOfType),
                                        actual = JsString.nameOfType
                                    )
                                )
                            }
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                    "when source is not the array type" - {
                        val source = JsString("")

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(JsArray.nameOfType),
                                    actual = JsString.nameOfType
                                )
                            )
                        }
                    }

                    "when the number of items is less than the number of elements in prefixItems" - {
                        val source = JsArray(JsString(FIRST_ITEM))

                        "then should return all elements read" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeSuccess success(location = LOCATION, value = listOf(FIRST_ITEM))
                        }
                    }

                    "when the number of items is equal to the number of elements in prefixItems" - {
                        val source =
                            JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))

                        "then should return all elements read" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = listOf(FIRST_ITEM, SECOND_ITEM)
                            )
                        }
                    }

                    "when the number of items is more than the number of elements in prefixItems" - {

                        "when additional items are of valid type" - {
                            val source = JsArray(
                                JsString(FIRST_ITEM),
                                JsString(SECOND_ITEM),
                                JsBoolean.valueOf(true)
                            )

                            "then should return all items read" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result shouldBeSuccess success(
                                    location = LOCATION,
                                    value = listOf(FIRST_ITEM, SECOND_ITEM, true)
                                )
                            }
                        }

                        "when additional items are of invalid type" - {
                            val source = JsArray(
                                JsString(FIRST_ITEM),
                                JsString(SECOND_ITEM),
                                JsString(THIRD_ITEM),
                                JsNumeric.valueOf(10)
                            )

                            "then should return all errors" {
                                val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                                result.shouldBeFailure(
                                    JsReaderResult.Failure.Cause(
                                        location = LOCATION.append(2),
                                        error = JsonErrors.InvalidType(
                                            expected = listOf(JsBoolean.nameOfType),
                                            actual = JsString.nameOfType
                                        )
                                    ),
                                    JsReaderResult.Failure.Cause(
                                        location = LOCATION.append(3),
                                        error = JsonErrors.InvalidType(
                                            expected = listOf(JsBoolean.nameOfType),
                                            actual = JsNumeric.Integer.nameOfType
                                        )
                                    )
                                )
                            }
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
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
