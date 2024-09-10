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
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.common.DummyArrayValidator
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.kotest.assertions.cause
import io.github.airflux.serialization.kotest.assertions.shouldBeFailure
import io.github.airflux.serialization.kotest.assertions.shouldBeSuccess
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ArrayReaderBuildersWithValidationTest : FreeSpec() {

    init {

        "The buildArrayReader function with validation" - {

            "when the reader was created" - {
                val reader: JsArrayReader<EB, OPTS, String> = buildArrayReader(
                    arrayItemsReader = arrayItemsReader(items = StringReader),
                    validator = VALIDATOR
                )

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ENV_WITH_FAIL_FAST_IS_TRUE

                    "when the source does not have any errors" - {
                        val source = JsArray(JsString(FIRST_STRING_ITEM), JsString(SECOND_STRING_ITEM))

                        "then the reader should return the collection of items" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeSuccess()
                            result.location shouldBe LOCATION
                            result.value shouldContainExactly listOf(FIRST_STRING_ITEM, SECOND_STRING_ITEM)
                        }
                    }

                    "when the source is not a array type" - {
                        val source: JsValue = JsString("")

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.ARRAY,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        }
                    }

                    "when the source has a validation error" - {
                        val source: JsValue = JsArray(JsString(FIRST_STRING_ITEM))

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Arrays.MinItems(expected = MIN_ARRAY_ITEMS, actual = 1)
                            )
                        }
                    }

                    "when the source has a read error" - {
                        val source = JsArray(
                            JsNumber.valueOf(FIRST_INT_ITEM.toString())!!,
                            JsString(SECOND_STRING_ITEM)
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        }
                    }

                    "when the source has a validation and a read errors" - {
                        val source: JsValue = JsArray(JsNumber.valueOf(FIRST_INT_ITEM.toString())!!)

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Arrays.MinItems(expected = MIN_ARRAY_ITEMS, actual = 1)
                            )
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ENV_WITH_FAIL_FAST_IS_FALSE

                    "when the source does not have any errors" - {
                        val source = JsArray(JsString(FIRST_STRING_ITEM), JsString(SECOND_STRING_ITEM))

                        "then the reader should return the collection of items" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeSuccess()
                            result.location shouldBe LOCATION
                            result.value shouldContainExactly listOf(FIRST_STRING_ITEM, SECOND_STRING_ITEM)
                        }
                    }

                    "when the source is not a array type" - {
                        val source: JsValue = JsString("")

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.ARRAY,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        }
                    }

                    "when the source has a validation error" - {
                        val source: JsValue = JsArray(JsString(FIRST_STRING_ITEM))

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Arrays.MinItems(expected = MIN_ARRAY_ITEMS, actual = 1)
                            )
                        }
                    }

                    "when the source has a read error" - {
                        val source = JsArray(
                            JsNumber.valueOf(FIRST_INT_ITEM.toString())!!,
                            JsString(SECOND_STRING_ITEM)
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        }
                    }

                    "when the source has a validation and a read errors" - {
                        val source: JsValue = JsArray(JsNumber.valueOf(FIRST_INT_ITEM.toString())!!)

                        "then the reader should return it errors" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result.shouldBeFailure(
                                cause(
                                    location = LOCATION,
                                    error = JsonErrors.Validation.Arrays.MinItems(
                                        expected = MIN_ARRAY_ITEMS,
                                        actual = 1
                                    )
                                ),
                                cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.NUMBER
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val FIRST_STRING_ITEM = "first"
        private const val SECOND_STRING_ITEM = "second"
        private const val FIRST_INT_ITEM = 10

        private const val MIN_ARRAY_ITEMS = 2

        private val ENV_WITH_FAIL_FAST_IS_TRUE =
            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = true)))
        private val ENV_WITH_FAIL_FAST_IS_FALSE =
            JsReaderEnv(JsReaderEnv.Config(errorBuilders = EB(), options = OPTS(failFast = false)))

        private val LOCATION: JsLocation = JsLocation
        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()

        private val VALIDATOR = DummyArrayValidator.minItems<EB, OPTS>(
            expected = MIN_ARRAY_ITEMS,
            error = { expected: Int, actual: Int ->
                JsonErrors.Validation.Arrays.MinItems(expected, actual)
            }
        )
    }

    private class EB : InvalidTypeErrorBuilder,
                       PathMissingErrorBuilder,
                       AdditionalItemsErrorBuilder {
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing

        override fun additionalItemsError(): JsReaderResult.Error = JsonErrors.AdditionalItems
    }

    private class OPTS(override val failFast: Boolean) : FailFastOption
}
