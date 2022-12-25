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

package io.github.airflux.serialization.dsl.reader.struct.property.specification

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.dummyIntReader
import io.github.airflux.serialization.common.dummyStringReader
import io.github.airflux.serialization.common.shouldBeFailure
import io.github.airflux.serialization.common.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.std.validator.condition.applyIfNotNull
import io.github.airflux.serialization.std.validator.string.IsNotEmptyStringValidator
import io.github.airflux.serialization.std.validator.string.StdStringValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class RequiredPropertySpecTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val CODE_PROPERTY_NAME = "code"

        private const val ID_VALUE_AS_UUID = "91a10692-7430-4d58-a465-633d45ea2f4b"
        private const val ID_VALUE_AS_INT = "10"

        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty
        private val StringReader = dummyStringReader<EB, Unit>()
        private val IntReader = dummyIntReader<EB, Unit>()
    }

    init {

        "The StructPropertySpec#Required type" - {

            "when creating the instance by a property name" - {
                val spec = required(name = ID_PROPERTY_NAME, reader = StringReader)

                "then the paths parameter must contain only the passed name" {
                    spec.path.items shouldContainExactly listOf(PropertyPath(ID_PROPERTY_NAME))
                }

                "when the reader has read a property named id" - {

                    "if the property value is not the null type" - {
                        val source = StructNode(ID_PROPERTY_NAME to StringNode(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the not-null value should be returned" {
                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_VALUE_AS_UUID
                            )
                        }
                    }
                }

                "when the property does not founded" - {
                    val source = StructNode(CODE_PROPERTY_NAME to StringNode(ID_VALUE_AS_UUID))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then an error should be returned" {
                        result shouldBeFailure ReaderResult.Failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.PathMissing
                        )
                    }
                }

                "when a read error occurred" - {
                    val source = StructNode(ID_PROPERTY_NAME to NumericNode.Integer.valueOf(10))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then should be returned a read error" {
                        result shouldBeFailure ReaderResult.Failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.InvalidType(
                                expected = listOf(StringNode.nameOfType),
                                actual = NumericNode.Integer.nameOfType
                            )
                        )
                    }
                }
            }

            "when creating the instance by a property path" - {
                val path = PropertyPath(ID_PROPERTY_NAME)
                val spec = required(path = path, reader = StringReader)

                "then the paths parameter must contain only the passed path" {
                    spec.path.items shouldContainExactly listOf(path)
                }

                "when the reader has read a property named id" - {

                    "if the property value is not the null type" - {
                        val source = StructNode(ID_PROPERTY_NAME to StringNode(ID_VALUE_AS_UUID))
                        val result = spec.reader.read(ENV, LOCATION, source)

                        "then the not-null value should be returned" {
                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                value = ID_VALUE_AS_UUID
                            )
                        }
                    }
                }

                "when the property does not founded" - {
                    val source = StructNode(CODE_PROPERTY_NAME to StringNode(ID_VALUE_AS_UUID))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then an error should be returned" {
                        result shouldBeFailure ReaderResult.Failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.PathMissing
                        )
                    }
                }

                "when an error occurs while reading" - {
                    val source = StructNode(ID_PROPERTY_NAME to NumericNode.Integer.valueOf(10))
                    val result = spec.reader.read(ENV, LOCATION, source)

                    "then should be returned a read error" {
                        result shouldBeFailure ReaderResult.Failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.InvalidType(
                                expected = listOf(StringNode.nameOfType),
                                actual = NumericNode.Integer.nameOfType
                            )
                        )
                    }
                }
            }

            "when the validator was added to the spec" - {
                val spec = required(name = ID_PROPERTY_NAME, reader = StringReader)
                val specWithValidator = spec.validation(StdStringValidator.isNotEmpty<EB, Unit>().applyIfNotNull())

                "when the reader has successfully read" - {

                    "then a value should be returned if validation is a success" {
                        val source = StructNode(ID_PROPERTY_NAME to StringNode(ID_VALUE_AS_UUID))

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result shouldBeSuccess ReaderResult.Success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_VALUE_AS_UUID
                        )
                    }

                    "then a validation error should be returned if validation is a failure" {
                        val source = StructNode(ID_PROPERTY_NAME to StringNode(""))

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result shouldBeFailure ReaderResult.Failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when an error occurs while reading" - {

                    "then should be returned a read error" {
                        val source = StructNode(ID_PROPERTY_NAME to NumericNode.Integer.valueOf(10))

                        val result = specWithValidator.reader.read(ENV, LOCATION, source)

                        result shouldBeFailure ReaderResult.Failure(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            error = JsonErrors.InvalidType(
                                expected = listOf(StringNode.nameOfType),
                                actual = NumericNode.Integer.nameOfType
                            )
                        )
                    }
                }
            }

            "when an alternative spec was added" - {
                val spec = required(name = ID_PROPERTY_NAME, reader = StringReader)
                val alt = required(name = ID_PROPERTY_NAME, reader = IntReader.map { it.toString() })
                val specWithAlternative = spec or alt

                "then the paths parameter must contain all elements from both spec" {
                    specWithAlternative.path.items shouldContainExactly listOf(PropertyPath(ID_PROPERTY_NAME))
                }

                "when the main reader has successfully read" - {
                    val source = StructNode(ID_PROPERTY_NAME to StringNode(ID_VALUE_AS_UUID))
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then a value should be returned" {
                        result shouldBeSuccess ReaderResult.Success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_VALUE_AS_UUID
                        )
                    }
                }

                "when the main reader has failure read" - {
                    val source = StructNode(ID_PROPERTY_NAME to NumericNode.Integer.valueOrNullOf(ID_VALUE_AS_INT)!!)
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then a value should be returned from the alternative reader" {
                        result shouldBeSuccess ReaderResult.Success(
                            location = LOCATION.append(ID_PROPERTY_NAME),
                            value = ID_VALUE_AS_INT
                        )
                    }
                }

                "when the alternative reader has failure read" - {
                    val source = StructNode(ID_PROPERTY_NAME to BooleanNode.True)
                    val result = specWithAlternative.reader.read(ENV, LOCATION, source)

                    "then should be returned all read errors" {
                        result shouldBeFailure listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(StringNode.nameOfType),
                                    actual = BooleanNode.nameOfType
                                )
                            ),
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(ID_PROPERTY_NAME),
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

    internal class EB : PathMissingErrorBuilder,
                        InvalidTypeErrorBuilder,
                        IsNotEmptyStringValidator.ErrorBuilder {

        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun isNotEmptyStringError(): ReaderResult.Error = JsonErrors.Validation.Strings.IsEmpty
    }
}
