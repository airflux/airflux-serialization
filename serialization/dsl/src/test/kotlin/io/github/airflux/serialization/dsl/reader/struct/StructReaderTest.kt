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

package io.github.airflux.serialization.dsl.reader.struct

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.core.value.valueOf
import io.github.airflux.serialization.dsl.common.DummyReader
import io.github.airflux.serialization.dsl.common.DummyStructValidatorBuilder
import io.github.airflux.serialization.dsl.common.JsonErrors
import io.github.airflux.serialization.dsl.common.kotest.shouldBeFailure
import io.github.airflux.serialization.dsl.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.dsl.reader.struct.property.specification.optional
import io.github.airflux.serialization.dsl.reader.struct.property.specification.required
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec

internal class StructReaderTest : FreeSpec() {

    companion object {

        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = 42
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "user"
        private const val IS_ACTIVE_PROPERTY_NAME = "isActive"
        private const val IS_ACTIVE_PROPERTY_VALUE = true

        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val StringReader: Reader<EB, OPTS, Unit, String> = DummyReader.string()
        private val IntReader: Reader<EB, OPTS, Unit, Int> = DummyReader.int()
    }

    init {

        "The StructReader type" - {

            "when was created reader" - {
                val validator = DummyStructValidatorBuilder.additionalProperties<EB, OPTS, Unit>(
                    nameProperties = setOf(ID_PROPERTY_NAME, NAME_PROPERTY_NAME),
                    error = JsonErrors.Validation.Struct.AdditionalProperties
                )
                val reader: Reader<EB, OPTS, Unit, DTO> = structReader {
                    validation(validator)

                    val id = property(required(name = ID_PROPERTY_NAME, reader = IntReader))
                    val name = property(optional(name = NAME_PROPERTY_NAME, reader = StringReader))
                    returns { _, _, location ->
                        DTO(id = +id, name = +name).toSuccess(location)
                    }
                }

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                    "when the source is not the struct type" - {
                        val source = StringNode("")

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(StructNode.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when the source contains all properties" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE)
                        )

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                            )
                        }
                    }

                    "when the source does not contain required properties" - {
                        val source = StructNode(
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE)
                        )

                        "then should return an error" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.PathMissing
                            )
                        }
                    }

                    "when the source does not contain optional properties" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE)
                        )

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = DTO(id = ID_PROPERTY_VALUE, name = null)
                            )
                        }
                    }

                    "when the source contains the required property of an invalid type" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                        )

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(NumericNode.Integer.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when the source contains the optional property of an invalid type" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true),
                        )

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(NAME_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(StringNode.nameOfType),
                                    actual = BooleanNode.nameOfType
                                )
                            )
                        }
                    }

                    "when the source contains all properties of an invalid type" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true)
                        )

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(NumericNode.Integer.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when an error occur of validation the structure" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to BooleanNode.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                    }

                    "when errors occur of validation the structure and reading properties" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true),
                            IS_ACTIVE_PROPERTY_NAME to BooleanNode.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                    "when the source is not the struct type" - {
                        val source = StringNode("")

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION,
                                error = JsonErrors.InvalidType(
                                    expected = listOf(StructNode.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when the source contains all properties" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE)
                        )

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                            )
                        }
                    }

                    "when the source does not contain required property" - {
                        val source = StructNode(
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                        )

                        "then should return an error" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.PathMissing
                            )
                        }
                    }

                    "when the source does not contain optional property" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE)
                        )

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeSuccess success(
                                location = LOCATION,
                                value = DTO(id = ID_PROPERTY_VALUE, name = null)
                            )
                        }
                    }

                    "when the source contains the required property of an invalid type" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                        )

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(ID_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(NumericNode.Integer.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when the source contains the optional property of an invalid type" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true),
                        )

                        "then the reader should return an error" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(NAME_PROPERTY_NAME),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(StringNode.nameOfType),
                                    actual = BooleanNode.nameOfType
                                )
                            )
                        }
                    }

                    "when the source contains all properties of an invalid type" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true)
                        )

                        "then the reader should return all errors" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result.shouldBeFailure(
                                ReadingResult.Failure.Cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumericNode.Integer.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                ),
                                ReadingResult.Failure.Cause(
                                    location = LOCATION.append(NAME_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StringNode.nameOfType),
                                        actual = BooleanNode.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when an error occur of validation the structure" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to BooleanNode.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result shouldBeFailure failure(
                                location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        }
                    }

                    "when errors occur of validation the structure and reading properties" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true),
                            IS_ACTIVE_PROPERTY_NAME to BooleanNode.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return all errors" {
                            val result = reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                            result.shouldBeFailure(
                                ReadingResult.Failure.Cause(
                                    location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                    error = JsonErrors.Validation.Struct.AdditionalProperties
                                ),
                                ReadingResult.Failure.Cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumericNode.Integer.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                ),
                                ReadingResult.Failure.Cause(
                                    location = LOCATION.append(NAME_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StringNode.nameOfType),
                                        actual = BooleanNode.nameOfType
                                    )
                                )
                            )
                        }
                    }
                }
            }

            "when was created reader and the result builder throw some exception" - {
                val reader: Reader<EB, OPTS, Unit, DTO> = structReader {
                    returns { _, _, _ ->
                        throw IllegalStateException()
                    }
                }

                val source = StructNode(
                    ID_PROPERTY_NAME to NumericNode.valueOf(ID_PROPERTY_VALUE),
                    NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                )

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                    "then it exception should be thrown out from the reader" {
                        shouldThrow<IllegalStateException> {
                            reader.read(envWithFailFastIsTrue, CONTEXT, LOCATION, source)
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                    "then it exception should be thrown out from the reader" {
                        shouldThrow<IllegalStateException> {
                            reader.read(envWithFailFastIsFalse, CONTEXT, LOCATION, source)
                        }
                    }
                }
            }
        }
    }

    internal data class DTO(val id: Int, val name: String?)

    internal class EB : InvalidTypeErrorBuilder,
        PathMissingErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReadingResult.Error = JsonErrors.PathMissing
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
