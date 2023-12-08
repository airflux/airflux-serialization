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
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.dsl.common.DummyArrayValidatorBuilder.Companion.minItems
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class ArrayReaderOnlyItemsTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM = "first"
        private const val SECOND_ITEM = "second"

        private val LOCATION: JsLocation = JsLocation
        private const val MIN_ITEMS = 2

        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()
    }

    init {

        "The ArrayReader type" - {

            "when a reader was created for only items" - {
                val reader: JsReader<EB, OPTS, List<String>> = arrayReader {
                    validation(
                        minItems(
                            expected = MIN_ITEMS,
                            error = { expected, actual -> JsonErrors.Validation.Arrays.MinItems(expected, actual) }
                        )
                    )
                    returns(items = StringReader)
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

                    "when no errors in the reader" - {
                        val source = JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = listOf(FIRST_ITEM, SECOND_ITEM)
                            )
                        }
                    }

                    "when error occur of validation the array" - {
                        val source = JsArray()

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Arrays.MinItems(
                                    expected = MIN_ITEMS,
                                    actual = source.size
                                )
                            )
                        }
                    }

                    "when error occur of validation the array and reading items" - {
                        val source = JsArray(JsBoolean.valueOf(true))

                        "then the reader should return validation error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Arrays.MinItems(
                                    expected = MIN_ITEMS,
                                    actual = source.size
                                )
                            )
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

                    "when no errors in the reader" - {
                        val source = JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = listOf(FIRST_ITEM, SECOND_ITEM)
                            )
                        }
                    }

                    "when error occur of validation the array" - {
                        val source = JsArray()

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Arrays.MinItems(
                                    expected = MIN_ITEMS,
                                    actual = source.size
                                )
                            )
                        }
                    }

                    "when error occur of validation the array and reading items" - {
                        val source = JsArray(JsBoolean.valueOf(true))

                        "then the reader should return all errors" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION,
                                    error = JsonErrors.Validation.Arrays.MinItems(
                                        expected = MIN_ITEMS,
                                        actual = source.size
                                    )
                                ),
                                JsReaderResult.Failure.Cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(JsString.nameOfType),
                                        actual = JsBoolean.nameOfType
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
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
