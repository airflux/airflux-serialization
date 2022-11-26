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

package io.github.airflux.serialization.dsl.reader.struct.builder

import io.github.airflux.serialization.common.DummyObjectValidatorBuilder
import io.github.airflux.serialization.common.DummyReader
import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.ObjectNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperty
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.defaultable
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.nullable
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.nullableWithDefault
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.optional
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.optionalWithDefault
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ObjectReaderBuilderTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "name"
        private const val USER_NAME = "user"
        private const val DEFAULT_VALUE = "none"
        private val DEFAULT = { _: ReaderEnv<EB, CTX> -> DEFAULT_VALUE }

        private val LOCATION = Location.empty
        private val MinPropertiesError = JsonErrors.Validation.Object.MinProperties(expected = 1, actual = 0)
    }

    init {

        "The ObjectReaderBuilder type" - {

            "when no errors in the reader" - {
                val validator = DummyObjectValidatorBuilder<EB, CTX>(
                    key = DummyObjectValidatorBuilder.key<EB, CTX, DummyObjectValidatorBuilder<EB, CTX>>(),
                    result = null
                )
                val reader: Reader<EB, CTX, DTO> = structReader {
                    validation {
                        +validator
                    }
                    val name = property(propertySpec(value = USER_NAME))
                    returns { _, _ ->
                        DTO(name = +name).success()
                    }
                }

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))

                    "then should return successful value" {
                        val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                        val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                        result as ReaderResult.Success
                        result.value shouldBe DTO(name = USER_NAME)
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), CTX(failFast = false))
                    "then should return successful value" {
                        val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                        val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                        result as ReaderResult.Success
                        result.value shouldBe DTO(name = USER_NAME)
                    }
                }
            }

            "when errors occur in the reader" - {

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))

                    "when source is not the object type" - {
                        val source = StringNode(USER_NAME)
                        val reader: Reader<EB, CTX, DTO> = structReader {
                            val name = property(propertySpec(value = USER_NAME))
                            returns { _, _ ->
                                DTO(name = +name).success()
                            }
                        }

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(
                                        expected = ValueNode.Type.OBJECT,
                                        actual = ValueNode.Type.STRING
                                    )
                                )
                            )
                        }
                    }

                    "when the validator returns an error" - {
                        val validator = DummyObjectValidatorBuilder<EB, CTX>(
                            key = DummyObjectValidatorBuilder.key<EB, CTX, DummyObjectValidatorBuilder<EB, CTX>>(),
                            result = ReaderResult.Failure(location = LOCATION, error = MinPropertiesError)
                        )
                        val reader: Reader<EB, CTX, DTO> = structReader {
                            validation {
                                +validator
                            }
                            val name = property(propertySpec(value = USER_NAME))
                            returns { _, _ ->
                                DTO(name = +name).success()
                            }
                        }

                        "then the reader should return the validation error" {
                            val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(location = LOCATION, error = MinPropertiesError)
                            )
                        }
                    }

                    "when the reader of an property returns an error" - {
                        val validator = DummyObjectValidatorBuilder<EB, CTX>(
                            key = DummyObjectValidatorBuilder.key<EB, CTX, DummyObjectValidatorBuilder<EB, CTX>>(),
                            result = null
                        )
                        val reader: Reader<EB, CTX, DTO> = structReader {
                            validation {
                                +validator
                            }
                            val name: ObjectProperty.Required<EB, CTX, String> =
                                property(propertySpec(error = JsonErrors.PathMissing))
                            returns { _, _ ->
                                DTO(name = +name).success()
                            }
                        }

                        "then the reader should return the validation error" {
                            val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(PROPERTY_NAME),
                                    error = JsonErrors.PathMissing
                                )
                            )
                        }
                    }

                    "when the validator and the reader of an property may return some errors" - {
                        val validator = DummyObjectValidatorBuilder<EB, CTX>(
                            key = DummyObjectValidatorBuilder.key<EB, CTX, DummyObjectValidatorBuilder<EB, CTX>>(),
                            result = ReaderResult.Failure(location = LOCATION, error = MinPropertiesError)
                        )
                        val reader: Reader<EB, CTX, DTO> = structReader {
                            validation {
                                +validator
                            }
                            val name: ObjectProperty.Required<EB, CTX, String> =
                                property(propertySpec(error = JsonErrors.PathMissing))
                            returns { _, _ ->
                                DTO(name = +name).success()
                            }
                        }

                        "then only an error of validation should be returns" {
                            val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                            val result = reader.read(envWithFailFastIsTrue, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(location = LOCATION, error = MinPropertiesError),
                            )
                        }
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), CTX(failFast = false))

                    "when source is not the object type" - {
                        val source = StringNode(USER_NAME)
                        val reader: Reader<EB, CTX, DTO> = structReader {
                            val name = property(propertySpec(value = USER_NAME))
                            returns { _, _ ->
                                DTO(name = +name).success()
                            }
                        }

                        "then the reader should return the invalid type error" {
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(
                                        expected = ValueNode.Type.OBJECT,
                                        actual = ValueNode.Type.STRING
                                    )
                                )
                            )
                        }
                    }

                    "when the validator returns an error" - {
                        val validator = DummyObjectValidatorBuilder<EB, CTX>(
                            key = DummyObjectValidatorBuilder.key<EB, CTX, DummyObjectValidatorBuilder<EB, CTX>>(),
                            result = ReaderResult.Failure(location = LOCATION, error = MinPropertiesError)
                        )
                        val reader: Reader<EB, CTX, DTO> = structReader {
                            validation {
                                +validator
                            }
                            val name = property(propertySpec(value = USER_NAME))
                            returns { _, _ ->
                                DTO(name = +name).success()
                            }
                        }

                        "then the reader should return the validation error" {
                            val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(location = LOCATION, error = MinPropertiesError)
                            )
                        }
                    }

                    "when the reader of an property returns an error" - {
                        val validator = DummyObjectValidatorBuilder<EB, CTX>(
                            key = DummyObjectValidatorBuilder.key<EB, CTX, DummyObjectValidatorBuilder<EB, CTX>>(),
                            result = null
                        )
                        val reader: Reader<EB, CTX, DTO> = structReader {
                            validation {
                                +validator
                            }
                            val name: ObjectProperty.Required<EB, CTX, String> =
                                property(propertySpec(error = JsonErrors.PathMissing))
                            returns { _, _ ->
                                DTO(name = +name).success()
                            }
                        }

                        "then the reader should return the validation error" {
                            val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(PROPERTY_NAME),
                                    error = JsonErrors.PathMissing
                                )
                            )
                        }
                    }

                    "when the validator and the reader of an property may return some errors" - {
                        val validator = DummyObjectValidatorBuilder<EB, CTX>(
                            key = DummyObjectValidatorBuilder.key<EB, CTX, DummyObjectValidatorBuilder<EB, CTX>>(),
                            result = ReaderResult.Failure(location = LOCATION, error = MinPropertiesError)
                        )
                        val reader: Reader<EB, CTX, DTO> = structReader {
                            validation {
                                +validator
                            }
                            val name: ObjectProperty.Required<EB, CTX, String> =
                                property(propertySpec(error = JsonErrors.PathMissing))
                            returns { _, _ ->
                                DTO(name = +name).success()
                            }
                        }

                        "then all error should be returns" {
                            val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                            val result = reader.read(envWithFailFastIsFalse, LOCATION, source)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(location = LOCATION, error = MinPropertiesError),
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(PROPERTY_NAME),
                                    error = JsonErrors.PathMissing
                                )
                            )
                        }
                    }
                }
            }

            "when some exception was thrown in the result builder" - {
                val reader: Reader<EB, CTX, DTO> = structReader {
                    property(propertySpec(value = USER_NAME))

                    returns { _, _ ->
                        throw IllegalStateException()
                    }
                }

                val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))

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

            "the StructNode#read extension-function" - {
                val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))
                val source = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))

                "when property is the required" - {
                    val property: ObjectProperty<EB, CTX> = ObjectProperty.Required(
                        required(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME)
                        )
                    )

                    "then function should return a result" {
                        val result = with(ObjectReader) {
                            source.read(envWithFailFastIsTrue, LOCATION, property)
                        }
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the defaultable" - {
                    val property: ObjectProperty<EB, CTX> = ObjectProperty.Defaultable(
                        defaultable(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME),
                            default = DEFAULT
                        )
                    )

                    "then function should return a result" {
                        val result = with(ObjectReader) {
                            source.read(envWithFailFastIsTrue, LOCATION, property)
                        }
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the optional" - {
                    val property: ObjectProperty<EB, CTX> = ObjectProperty.Optional(
                        optional(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME)
                        )
                    )

                    "then function should return a result" {
                        val result = with(ObjectReader) {
                            source.read(envWithFailFastIsTrue, LOCATION, property)
                        }
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the optional with default" - {
                    val property: ObjectProperty<EB, CTX> = ObjectProperty.OptionalWithDefault(
                        optionalWithDefault(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME),
                            default = DEFAULT
                        )
                    )

                    "then function should return a result" {
                        val result = with(ObjectReader) {
                            source.read(envWithFailFastIsTrue, LOCATION, property)
                        }
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the nullable" - {
                    val property: ObjectProperty<EB, CTX> = ObjectProperty.Nullable(
                        nullable(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME)
                        )
                    )

                    "then function should return a result" {
                        val result = with(ObjectReader) {
                            source.read(envWithFailFastIsTrue, LOCATION, property)
                        }
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the nullable with default" - {
                    val property: ObjectProperty<EB, CTX> = ObjectProperty.NullableWithDefault(
                        nullableWithDefault(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME),
                            default = DEFAULT
                        )
                    )

                    "then function should return a result" {
                        val result = with(ObjectReader) {
                            source.read(envWithFailFastIsTrue, LOCATION, property)
                        }
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }
            }
        }
    }

    private fun <T : Any> propertySpec(value: T) = required(name = PROPERTY_NAME, reader = createReader(value = value))

    private fun <T : Any> propertySpec(error: ReaderResult.Error) = required(
        name = PROPERTY_NAME,
        reader = DummyReader<EB, CTX, T>(
            result = ReaderResult.Failure(
                location = LOCATION.append(PROPERTY_NAME),
                error = error
            )
        )
    )

    private fun <T : Any> createReader(value: T): DummyReader<EB, CTX, T> =
        DummyReader(result = ReaderResult.Success(value = value))

    internal data class DTO(val name: String)

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder {
        override fun invalidTypeError(expected: ValueNode.Type, actual: ValueNode.Type): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing
    }

    internal class CTX(override val failFast: Boolean) : FailFastOption
}
