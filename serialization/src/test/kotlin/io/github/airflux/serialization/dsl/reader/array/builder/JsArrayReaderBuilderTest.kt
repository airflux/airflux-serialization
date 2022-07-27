/*
 * Copyright 2021-2022 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.reader.array.builder

import io.github.airflux.serialization.common.DummyArrayValidatorBuilder
import io.github.airflux.serialization.common.DummyReader
import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.FailFast
import io.github.airflux.serialization.core.reader.result.JsError
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.nonNullable
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class JsArrayReaderBuilderTest : FreeSpec() {

    companion object {
        private const val ATTRIBUTE_NAME = "items"
        private const val FIRST_ITEM = "first"
        private const val SECOND_ITEM = "second"
        private const val USER_NAME = "user"

        private val CONTEXT = JsReaderContext(
            listOf(
                AdditionalItemsErrorBuilder { JsonErrors.AdditionalItems },
                InvalidTypeErrorBuilder(JsonErrors::InvalidType)
            )
        )
        private val LOCATION = JsLocation.empty

        private val MinItemsError = JsonErrors.Validation.Arrays.MinItems(expected = 1, actual = 0)
    }

    init {

        "The JsArrayReaderBuilder type" - {

            "when no errors in the reader" - {
                val reader = arrayReader<String> {
                    validation {
                        DummyArrayValidatorBuilder(
                            key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                            result = null
                        )
                    }
                    returns(items = itemSpec())
                }

                "then should returns successful value" {
                    val input = JsArray(JsString(io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.FIRST_ITEM), JsString(
                        io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.SECOND_ITEM
                    ))
                    val result = reader.read(context = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.CONTEXT, location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION, input)
                    result as JsResult.Success
                    result.value shouldContainExactly listOf(
                        io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.FIRST_ITEM,
                        io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.SECOND_ITEM
                    )
                }
            }

            "when errors occur in the reader" - {

                "when input is not the object type" - {
                    val input = JsString(io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.USER_NAME)
                    val reader = arrayReader<String> {
                        returns(items = itemSpec())
                    }

                    "then the reader should return the invalid type error" {
                        val result = reader.read(context = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.CONTEXT, location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION, input)
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.ARRAY,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        )
                    }
                }

                "when fail-fast is true" - {
                    val contextWithFailFastTrue = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.CONTEXT + FailFast(true)

                    "when the validator returns an error" - {
                        val reader = arrayReader<String> {
                            validation {
                                +DummyArrayValidatorBuilder(
                                    key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                                    result = JsResult.Failure(
                                        location = LOCATION.append(
                                            ATTRIBUTE_NAME
                                        ),
                                        error = MinItemsError
                                    )
                                )
                            }
                            returns(itemSpec(JsonErrors.PathMissing))
                        }

                        "then the reader should return the validation error" {
                            val input = JsArray(JsString(io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.FIRST_ITEM))
                            val result = reader.read(context = contextWithFailFastTrue, location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION, input)
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION.append(
                                        io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.ATTRIBUTE_NAME
                                    ),
                                    error = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.MinItemsError
                                )
                            )
                        }
                    }

                    "when the reader of items returns an error" - {
                        val reader = arrayReader<String> {
                            validation {
                                +DummyArrayValidatorBuilder(
                                    key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                                    result = null
                                )
                            }
                            returns(itemSpec(JsonErrors.PathMissing))
                        }

                        "then the reader should return the validation error" {
                            val input = JsArray(JsString(io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.FIRST_ITEM))
                            val result = reader.read(context = contextWithFailFastTrue, location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION, input)
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION.append(
                                        io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.ATTRIBUTE_NAME
                                    ).append(0),
                                    error = JsonErrors.PathMissing
                                )
                            )
                        }
                    }
                }

                "when fail-fast is false" - {

                    "when only the validator returns an error" - {
                        val contextWithFailFastFalse = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.CONTEXT + FailFast(false)
                        val reader = arrayReader<String> {
                            validation {
                                +DummyArrayValidatorBuilder(
                                    key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                                    result = JsResult.Failure(
                                        location = LOCATION.append(
                                            ATTRIBUTE_NAME
                                        ),
                                        error = MinItemsError
                                    )
                                )
                            }
                            returns(itemSpec())
                        }

                        "then the reader should return the validation error" {
                            val input = JsArray(JsString(io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.FIRST_ITEM))
                            val result = reader.read(context = contextWithFailFastFalse, location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION, input)
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION.append(
                                        io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.ATTRIBUTE_NAME
                                    ),
                                    error = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.MinItemsError
                                )
                            )
                        }
                    }

                    "when the validator and the reader of items return errors" - {
                        val contextWithFailFastFalse = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.CONTEXT + FailFast(false)
                        val reader = arrayReader<String> {
                            validation {
                                +DummyArrayValidatorBuilder(
                                    key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                                    result = JsResult.Failure(
                                        location = LOCATION.append(
                                            ATTRIBUTE_NAME
                                        ),
                                        error = MinItemsError
                                    )
                                )
                            }
                            returns(itemSpec(JsonErrors.PathMissing))
                        }

                        "then all error should be returns" {
                            val input = JsArray(JsString(io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.FIRST_ITEM))
                            val result = reader.read(context = contextWithFailFastFalse, location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION, input)
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION.append(
                                        io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.ATTRIBUTE_NAME
                                    ),
                                    error = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.MinItemsError
                                ),
                                JsResult.Failure.Cause(
                                    location = io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.LOCATION.append(
                                        io.github.airflux.serialization.dsl.reader.array.builder.JsArrayReaderBuilderTest.Companion.ATTRIBUTE_NAME
                                    ).append(0),
                                    error = JsonErrors.PathMissing
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun itemSpec() = nonNullable { _, location, input ->
        (input as JsString).get.success(location)
    }

    fun <T : Any> itemSpec(error: JsError) = nonNullable(
        reader = DummyReader<T>(
            result = JsResult.Failure(location = LOCATION.append(
                ATTRIBUTE_NAME
            ).append(0), error = error)
        )
    )
}
