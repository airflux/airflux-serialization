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

package io.github.airflux.serialization.dsl.reader.struct

import io.github.airflux.serialization.common.DummyStructValidatorBuilder
import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.dummyIntReader
import io.github.airflux.serialization.common.dummyStringReader
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.struct.property.specification.optional
import io.github.airflux.serialization.dsl.reader.struct.property.specification.required
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class StructReaderTest : FreeSpec() {

    companion object {

        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = 42
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "user"
        private const val IS_ACTIVE_PROPERTY_NAME = "isActive"
        private const val IS_ACTIVE_PROPERTY_VALUE = true

        private val LOCATION = Location.empty
        private val StringReader = dummyStringReader<EB, CTX>()
        private val IntReader = dummyIntReader<EB, CTX>()
    }

    init {

        "The StructReader type" - {

            "when was created reader" - {
                val validator = DummyStructValidatorBuilder.additionalProperties<EB, CTX>(
                    nameProperties = setOf(ID_PROPERTY_NAME, NAME_PROPERTY_NAME),
                    error = JsonErrors.Validation.Struct.AdditionalProperties
                )
                val reader: Reader<EB, CTX, DTO> = structReader {
                    validation(validator)

                    val id = property(required(name = ID_PROPERTY_NAME, reader = IntReader))
                    val name = property(optional(name = NAME_PROPERTY_NAME, reader = StringReader))
                    returns { _, location ->
                        DTO(id = +id, name = +name).success(location)
                    }
                }

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))

                    "when source is not the struct type" - {
                        val source = StringNode("")

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StructNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when no errors in the reader" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumberNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                        )

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Success
                            result.value shouldBe DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                        }
                    }

                    "when error occur of reading properties the id" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumberNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when error occur of reading property the name" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumberNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(NAME_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StringNode.nameOfType),
                                        actual = BooleanNode.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when errors occur of reading properties the id and the name" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true)
                        )

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumberNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when error occur of validation the structure" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumberNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to BooleanNode.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                    error = JsonErrors.Validation.Struct.AdditionalProperties
                                )
                            )
                        }
                    }

                    "when errors occur of validation the structure and reading properties the id and the name" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true),
                            IS_ACTIVE_PROPERTY_NAME to BooleanNode.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                    error = JsonErrors.Validation.Struct.AdditionalProperties
                                )
                            )
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), CTX(failFast = false))

                    "when source is not the struct type" - {
                        val source = StringNode("")

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StructNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when no errors in the reader" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumberNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                        )

                        "then should return successful value" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Success
                            result.value shouldBe DTO(id = ID_PROPERTY_VALUE, name = NAME_PROPERTY_VALUE)
                        }
                    }

                    "when error occur of reading properties the id" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumberNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when error occur of reading property the name" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumberNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(NAME_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StringNode.nameOfType),
                                        actual = BooleanNode.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when errors occur of reading properties the id and the name" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true)
                        )

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumberNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                ),
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(NAME_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StringNode.nameOfType),
                                        actual = BooleanNode.nameOfType
                                    )
                                )
                            )
                        }
                    }

                    "when error occur of validation the structure" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to NumberNode.valueOf(ID_PROPERTY_VALUE),
                            NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                            IS_ACTIVE_PROPERTY_NAME to BooleanNode.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return it error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                    error = JsonErrors.Validation.Struct.AdditionalProperties
                                )
                            )
                        }
                    }

                    "when errors occur of validation the structure and reading properties the id and the name" - {
                        val source = StructNode(
                            ID_PROPERTY_NAME to StringNode(""),
                            NAME_PROPERTY_NAME to BooleanNode.valueOf(true),
                            IS_ACTIVE_PROPERTY_NAME to BooleanNode.valueOf(IS_ACTIVE_PROPERTY_VALUE),
                        )

                        "then the reader should return first error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(IS_ACTIVE_PROPERTY_NAME),
                                    error = JsonErrors.Validation.Struct.AdditionalProperties
                                ),
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(ID_PROPERTY_NAME),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumberNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                ),
                                ReaderResult.Failure.Cause(
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
                val reader: Reader<EB, CTX, DTO> = structReader {
                    returns { _, _ ->
                        throw IllegalStateException()
                    }
                }

                val source = StructNode(
                    ID_PROPERTY_NAME to NumberNode.valueOf(ID_PROPERTY_VALUE),
                    NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                )

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))

                    "then it exception should be thrown out from the reader" {
                        shouldThrow<IllegalStateException> {
                            reader.read(envWithFailFastIsTrue, LOCATION, source)
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), CTX(failFast = false))

                    "then it exception should be thrown out from the reader" {
                        shouldThrow<IllegalStateException> {
                            reader.read(envWithFailFastIsFalse, LOCATION, source)
                        }
                    }
                }
            }
        }
    }

    internal data class DTO(val id: Int, val name: String?)

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing
    }

    internal class CTX(override val failFast: Boolean) : FailFastOption
}
