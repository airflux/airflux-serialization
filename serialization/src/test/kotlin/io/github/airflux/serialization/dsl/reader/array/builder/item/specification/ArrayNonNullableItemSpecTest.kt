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

package io.github.airflux.serialization.dsl.reader.array.builder.item.specification

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.std.reader.IntReader
import io.github.airflux.serialization.std.reader.StringReader
import io.github.airflux.serialization.std.validator.string.IsNotEmptyStringValidator
import io.github.airflux.serialization.std.validator.string.StdStringValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ArrayNonNullableItemSpecTest : FreeSpec() {

    companion object {
        private const val ID_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_VALUE_AS_INT = "10"

        private val CONTEXT =
            ReaderContext(
                listOf(
                    IsNotEmptyStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty },
                    PathMissingErrorBuilder { JsonErrors.PathMissing },
                    InvalidTypeErrorBuilder(JsonErrors::InvalidType)
                )
            )
        private val LOCATION = Location.empty
    }

    init {
        "The ArrayItemSpec#NonNullable type" - {

            "when creating the instance of the spec" - {
                val spec = nonNullable(reader = StringReader)

                "when the reader has read an item" - {

                    "when the reader has successfully read" - {
                        val input = StringNode(ID_VALUE_AS_UUID)
                        val result = spec.reader.read(CONTEXT, LOCATION, input)

                        "then a value should be returned" {
                            result as JsResult.Success<String>
                            result.value shouldBe ID_VALUE_AS_UUID
                        }
                    }

                    "when a read error occurred" - {
                        val input = NumberNode.valueOf(10)
                        val result = spec.reader.read(CONTEXT, LOCATION, input)

                        "then should be returned a read error" {
                            result as JsResult.Failure
                            result.causes shouldContainExactly listOf(
                                JsResult.Failure.Cause(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(
                                        expected = ValueNode.Type.STRING,
                                        actual = ValueNode.Type.NUMBER
                                    )
                                )
                            )
                        }
                    }
                }
            }

            "when the validator was added to the spec" - {
                val spec = ArrayItemSpec.NonNullable(reader = StringReader)
                val specWithValidator = spec.validation(StdStringValidator.isNotEmpty)

                "when the reader has successfully read" - {

                    "then a value should be returned if validation is a success" {
                        val input = StringNode(ID_VALUE_AS_UUID)

                        val result = specWithValidator.reader.read(CONTEXT, LOCATION, input)

                        result as JsResult.Success<String>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }

                    "then a validation error should be returned if validation is a failure" {
                        val input = StringNode("")

                        val result = specWithValidator.reader.read(CONTEXT, LOCATION, input)

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.IsEmpty
                            )
                        )
                    }
                }

                "when an error occurs while reading" - {

                    "then should be returned a read error" {
                        val input = NumberNode.valueOf(10)

                        val result = specWithValidator.reader.read(CONTEXT, LOCATION, input)

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.NUMBER
                                )
                            )
                        )
                    }
                }
            }

            "when an alternative spec was added" - {
                val spec = nonNullable(reader = StringReader)
                val alt = nonNullable(reader = IntReader.map { it.toString() })
                val specWithAlternative = spec or alt

                "when the main reader has successfully read" - {
                    val input = StringNode(ID_VALUE_AS_UUID)
                    val result = specWithAlternative.reader.read(CONTEXT, LOCATION, input)

                    "then a value should be returned" {
                        println(result)
                        result as JsResult.Success<String>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }
                }

                "when the main reader has failure read" - {
                    val input = NumberNode.valueOf(ID_VALUE_AS_INT)!!
                    val result = specWithAlternative.reader.read(CONTEXT, LOCATION, input)

                    "then a value should be returned from the alternative reader" {
                        result as JsResult.Success<String>
                        result.value shouldBe ID_VALUE_AS_INT
                    }
                }

                "when the alternative reader has failure read" - {
                    val input = BooleanNode.True
                    val result = specWithAlternative.reader.read(CONTEXT, LOCATION, input)

                    "then should be returned all read errors" {
                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.BOOLEAN
                                )
                            ),
                            JsResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.NUMBER,
                                    actual = ValueNode.Type.BOOLEAN
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}
