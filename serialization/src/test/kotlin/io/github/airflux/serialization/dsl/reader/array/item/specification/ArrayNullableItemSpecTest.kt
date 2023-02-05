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

package io.github.airflux.serialization.dsl.reader.array.item.specification

import io.github.airflux.serialization.common.DummyReader
import io.github.airflux.serialization.common.DummyValidator
import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.kotest.shouldBeFailure
import io.github.airflux.serialization.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.std.validator.condition.applyIfNotNull
import io.kotest.core.spec.style.FreeSpec

internal class ArrayNullableItemSpecTest : FreeSpec() {

    companion object {
        private const val ID_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_VALUE_AS_INT = "10"

        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val StringReader = DummyReader.string<EB, Unit, Unit>()
        private val IntReader = DummyReader.int<EB, Unit, Unit>()

        private val IsNotEmptyStringValidator =
            DummyValidator.isNotEmptyString<EB, Unit, Unit> { JsonErrors.Validation.Strings.IsEmpty }
    }

    init {

        "The ArrayItemSpec#Nullable type" - {

            "when creating the instance of the spec" - {
                val spec = nullable(reader = StringReader)

                "when the reader has read an item" - {

                    "when the reader has successfully read" - {

                        "if the property value is not the null type" - {
                            val source = StringNode(ID_VALUE_AS_UUID)
                            val result = spec.reader.read(ENV, CONTEXT, LOCATION, source)

                            "then the not-null value should be returned" {
                                result shouldBeSuccess ReaderResult.Success(
                                    location = LOCATION,
                                    value = ID_VALUE_AS_UUID
                                )
                            }
                        }

                        "if the property value is the null type" - {
                            val source = NullNode
                            val result = spec.reader.read(ENV, CONTEXT, LOCATION, source)

                            "then the null value should be returned" {
                                result shouldBeSuccess ReaderResult.Success(
                                    location = LOCATION,
                                    value = null
                                )
                            }
                        }
                    }

                    "when a read error occurred" - {
                        val source = NumericNode.Integer.valueOf(10)
                        val result = spec.reader.read(ENV, CONTEXT, LOCATION, source)

                        "then should be returned a read error" {
                            result shouldBeFailure ReaderResult.Failure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(StringNode.nameOfType),
                                    actual = NumericNode.Integer.nameOfType
                                )
                            )
                        }
                    }
                }
            }

            "when the validator was added to the spec" - {
                val spec = nullable(reader = StringReader)
                val specWithValidator =
                    spec.validation(IsNotEmptyStringValidator.applyIfNotNull())

                "when the reader has successfully read" - {

                    "then a value should be returned if validation is a success" {
                        val source = StringNode(ID_VALUE_AS_UUID)

                        val result = specWithValidator.reader.read(ENV, CONTEXT, LOCATION, source)

                        result shouldBeSuccess ReaderResult.Success(
                            location = LOCATION,
                            value = ID_VALUE_AS_UUID
                        )
                    }

                    "then a validation error should be returned if validation is a failure" {
                        val source = StringNode("")

                        val result = specWithValidator.reader.read(ENV, CONTEXT, LOCATION, source)

                        result shouldBeFailure ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when an error occurs while reading" - {

                    "then should be returned a read error" {
                        val source = NumericNode.Integer.valueOf(10)

                        val result = specWithValidator.reader.read(ENV, CONTEXT, LOCATION, source)

                        result shouldBeFailure ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.InvalidType(
                                expected = listOf(StringNode.nameOfType),
                                actual = NumericNode.Integer.nameOfType
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
                    val result = specWithAlternative.reader.read(ENV, CONTEXT, LOCATION, source)

                    "then a value should be returned" {
                        result shouldBeSuccess ReaderResult.Success(
                            location = LOCATION,
                            value = ID_VALUE_AS_UUID
                        )
                    }
                }

                "when the main reader has failure read" - {
                    val source = NumericNode.Integer.valueOrNullOf(ID_VALUE_AS_INT)!!
                    val result = specWithAlternative.reader.read(ENV, CONTEXT, LOCATION, source)

                    "then a value should be returned from the alternative reader" {
                        result shouldBeSuccess ReaderResult.Success(
                            location = LOCATION,
                            value = ID_VALUE_AS_INT
                        )
                    }
                }

                "when the alternative reader has failure read" - {
                    val source = BooleanNode.True
                    val result = specWithAlternative.reader.read(ENV, CONTEXT, LOCATION, source)

                    "then should be returned all read errors" {
                        result.shouldBeFailure(
                            ReaderResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(StringNode.nameOfType),
                                    actual = BooleanNode.nameOfType
                                )
                            ),
                            ReaderResult.Failure.Cause(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(NumericNode.Integer.nameOfType),
                                    actual = BooleanNode.nameOfType
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
