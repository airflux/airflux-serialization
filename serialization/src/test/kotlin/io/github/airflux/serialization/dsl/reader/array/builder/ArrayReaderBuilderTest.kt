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
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.FailFast
import io.github.airflux.serialization.core.reader.result.JsError
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.nonNullable
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class ArrayReaderBuilderTest : FreeSpec() {

    companion object {
        private const val ATTRIBUTE_NAME = "items"
        private const val FIRST_ITEM = "first"
        private const val SECOND_ITEM = "second"
        private const val USER_NAME = "user"

        private val CONTEXT = ReaderContext(
            listOf(
                AdditionalItemsErrorBuilder { JsonErrors.AdditionalItems },
                InvalidTypeErrorBuilder(JsonErrors::InvalidType)
            )
        )
        private val LOCATION = JsLocation.empty

        private val MinItemsError = JsonErrors.Validation.Arrays.MinItems(expected = 1, actual = 0)
    }

    init {

        "The ArrayReaderBuilder type" - {

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

                "then should return successful value" {
                    val input = ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                    val result = reader.read(context = CONTEXT, location = LOCATION, input)
                    result as JsResult.Success
                    result.value shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM)
                }
            }

            "when errors occur in the reader" - {

                "when input is not the object type" - {
                    val input = StringNode(USER_NAME)
                    val reader = arrayReader<String> {
                        returns(items = itemSpec())
                    }

                    "then the reader should return the invalid type error" {
                        val result = reader.read(context = CONTEXT, location = LOCATION, input)
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.ARRAY,
                                    actual = ValueNode.Type.STRING
                                )
                            )
                        )
                    }
                }

                "when fail-fast is true" - {
                    val contextWithFailFastTrue = CONTEXT + FailFast(true)

                    "when the validator returns an error" - {
                        val reader = arrayReader<String> {
                            validation {
                                +DummyArrayValidatorBuilder(
                                    key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                                    result = JsResult.Failure(
                                        location = LOCATION.append(ATTRIBUTE_NAME),
                                        error = MinItemsError
                                    )
                                )
                            }
                            returns(itemSpec(JsonErrors.PathMissing))
                        }

                        "then the reader should return the validation error" {
                            val input = ArrayNode(StringNode(FIRST_ITEM))
                            val result = reader.read(context = contextWithFailFastTrue, location = LOCATION, input)
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = LOCATION.append(ATTRIBUTE_NAME),
                                    error = MinItemsError
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
                            val input = ArrayNode(StringNode(FIRST_ITEM))
                            val result = reader.read(context = contextWithFailFastTrue, location = LOCATION, input)
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = LOCATION.append(ATTRIBUTE_NAME).append(0),
                                    error = JsonErrors.PathMissing
                                )
                            )
                        }
                    }
                }

                "when fail-fast is false" - {

                    "when only the validator returns an error" - {
                        val contextWithFailFastFalse = CONTEXT + FailFast(false)
                        val reader = arrayReader<String> {
                            validation {
                                +DummyArrayValidatorBuilder(
                                    key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                                    result = JsResult.Failure(
                                        location = LOCATION.append(ATTRIBUTE_NAME),
                                        error = MinItemsError
                                    )
                                )
                            }
                            returns(itemSpec())
                        }

                        "then the reader should return the validation error" {
                            val input = ArrayNode(StringNode(FIRST_ITEM))
                            val result = reader.read(context = contextWithFailFastFalse, location = LOCATION, input)
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = LOCATION.append(ATTRIBUTE_NAME),
                                    error = MinItemsError
                                )
                            )
                        }
                    }

                    "when the validator and the reader of items return errors" - {
                        val contextWithFailFastFalse = CONTEXT + FailFast(false)
                        val reader = arrayReader<String> {
                            validation {
                                +DummyArrayValidatorBuilder(
                                    key = DummyArrayValidatorBuilder.key<DummyArrayValidatorBuilder>(),
                                    result = JsResult.Failure(
                                        location = LOCATION.append(ATTRIBUTE_NAME),
                                        error = MinItemsError
                                    )
                                )
                            }
                            returns(itemSpec(JsonErrors.PathMissing))
                        }

                        "then all error should be returns" {
                            val input = ArrayNode(StringNode(FIRST_ITEM))
                            val result = reader.read(context = contextWithFailFastFalse, location = LOCATION, input)
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = LOCATION.append(ATTRIBUTE_NAME),
                                    error = MinItemsError
                                ),
                                JsResult.Failure.Cause(
                                    location = LOCATION.append(ATTRIBUTE_NAME).append(0),
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
        (input as StringNode).get.success(location)
    }

    fun <T : Any> itemSpec(error: JsError) = nonNullable(
        reader = DummyReader<T>(
            result = JsResult.Failure(
                location = LOCATION.append(
                    ATTRIBUTE_NAME
                ).append(0), error = error
            )
        )
    )
}
