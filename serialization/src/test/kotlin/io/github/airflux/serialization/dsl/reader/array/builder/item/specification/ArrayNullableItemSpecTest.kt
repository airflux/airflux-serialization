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
import io.github.airflux.serialization.common.dummyIntReader
import io.github.airflux.serialization.common.dummyStringReader
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.std.validator.condition.applyIfNotNull
import io.github.airflux.serialization.std.validator.string.IsNotEmptyStringValidator
import io.github.airflux.serialization.std.validator.string.StdStringValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ArrayNullableItemSpecTest : FreeSpec() {

    companion object {
        private const val ID_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_VALUE_AS_INT = "10"

        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty
        private val StringReader = dummyStringReader<EB, Unit>()
        private val IntReader = dummyIntReader<EB, Unit>()
    }

    init {

        "The ArrayItemSpec#Nullable type" - {

            "when creating the instance of the spec" - {
                val spec = nullable(reader = StringReader)

                "when the reader has read an item" - {

                    "when the reader has successfully read" - {

                        "if the property value is not the null type" - {
                            val source = StringNode(ID_VALUE_AS_UUID)
                            val result = spec.reader.read(ENV, LOCATION, source)

                            "then the not-null value should be returned" {
                                result as ReaderResult.Success<String?>
                                result.value shouldBe ID_VALUE_AS_UUID
                            }
                        }

                        "if the property value is the null type" - {
                            val source = NullNode
                            val result = spec.reader.read(ENV, LOCATION, source)

                            "then the null value should be returned" {
                                result as ReaderResult.Success<String?>
                                result.value.shouldBeNull()
                            }
                        }
                    }

                    "when a read error occurred" - {
                        val source = NumberNode.valueOf(10)
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then should be returned a read error" {
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
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
                val spec = nullable(reader = StringReader)
                val specWithValidator = spec.validation(StdStringValidator.isNotEmpty<EB, Unit>().applyIfNotNull())

                "when the reader has successfully read" - {

                    "then a value should be returned if validation is a success" {
                        val source = StringNode(ID_VALUE_AS_UUID)

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result as ReaderResult.Success<String?>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }

                    "then a validation error should be returned if validation is a failure" {
                        val source = StringNode("")

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.IsEmpty
                            )
                        )
                    }
                }

                "when an error occurs while reading" - {

                    "then should be returned a read error" {
                        val source = NumberNode.valueOf(10)

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
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
                val spec = nullable(reader = StringReader)
                val alt = nullable(reader = IntReader.map { it.toString() })
                val specWithAlternative = spec or alt

                "when the main reader has successfully read" - {
                    val source = StringNode(ID_VALUE_AS_UUID)
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then a value should be returned" {
                        result as ReaderResult.Success<String?>
                        result.value shouldBe ID_VALUE_AS_UUID
                    }
                }

                "when the main reader has failure read" - {
                    val source = NumberNode.valueOf(ID_VALUE_AS_INT)!!
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then a value should be returned from the alternative reader" {
                        result as ReaderResult.Success<String?>
                        result.value shouldBe ID_VALUE_AS_INT
                    }
                }

                "when the alternative reader has failure read" - {
                    val source = BooleanNode.True
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then should be returned all read errors" {
                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.BOOLEAN
                                )
                            ),
                            ReaderResult.Failure.Cause(
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

    internal class EB : InvalidTypeErrorBuilder,
                        IsNotEmptyStringValidator.ErrorBuilder {

        override fun invalidTypeError(expected: ValueNode.Type, actual: ValueNode.Type): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun isNotEmptyStringError(): ReaderResult.Error = JsonErrors.Validation.Strings.IsEmpty
    }
}
