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
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.FailFast
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.ObjectNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.reader.context.exception.ExceptionsHandler
import io.github.airflux.serialization.dsl.reader.context.exception.ExceptionsHandlerBuilder
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

        private val CONTEXT = ReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private val LOCATION = Location.empty

        private val MinPropertiesError = JsonErrors.Validation.Object.MinProperties(expected = 1, actual = 0)
    }

    init {

        "The ObjectReaderBuilder type" - {

            "when no errors in the reader" - {
                val validator = DummyObjectValidatorBuilder(
                    key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                    result = null
                )
                val reader = reader<DTO> {
                    validation {
                        +validator
                    }
                    val name = property(propertySpec(value = USER_NAME))
                    returns { _, location ->
                        DTO(name = +name).success(location)
                    }
                }

                "then should return successful value" {
                    val input = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                    val result = reader.read(context = CONTEXT, location = LOCATION, input)
                    result as ReaderResult.Success
                    result.value shouldBe DTO(name = USER_NAME)
                }
            }

            "when errors occur in the reader" - {

                "when input is not the object type" - {
                    val input = StringNode(USER_NAME)
                    val reader = reader<DTO> {
                        val name = property(propertySpec(value = USER_NAME))
                        returns { _, location ->
                            DTO(name = +name).success(location)
                        }
                    }

                    "then the reader should return the invalid type error" {
                        val result = reader.read(context = CONTEXT, location = LOCATION, input)
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

                "when fail-fast is true" - {
                    val contextWithFailFastTrue = CONTEXT + FailFast(true)

                    "when the validator returns an error" - {
                        val validator = DummyObjectValidatorBuilder(
                            key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                            result = ReaderResult.Failure(location = LOCATION, error = MinPropertiesError)
                        )
                        val reader = reader<DTO> {
                            validation {
                                +validator
                            }
                            val name = property(propertySpec(value = USER_NAME))
                            returns { _, location ->
                                DTO(name = +name).success(location)
                            }
                        }

                        "then the reader should return the validation error" {
                            val input = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                            val result = reader.read(context = contextWithFailFastTrue, location = LOCATION, input)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(location = LOCATION, error = MinPropertiesError)
                            )
                        }
                    }

                    "when the reader of an property returns an error" - {
                        val validator = DummyObjectValidatorBuilder(
                            key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                            result = null
                        )
                        val reader = reader<DTO> {
                            validation {
                                +validator
                            }
                            val name: ObjectProperty.Required<String> =
                                property(propertySpec(error = JsonErrors.PathMissing))
                            returns { _, location ->
                                DTO(name = +name).success(location)
                            }
                        }

                        "then the reader should return the validation error" {
                            val input = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                            val result = reader.read(context = contextWithFailFastTrue, location = LOCATION, input)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(PROPERTY_NAME),
                                    error = JsonErrors.PathMissing
                                )
                            )
                        }
                    }
                }

                "when fail-fast is false" - {
                    val contextWithFailFastFalse = CONTEXT + FailFast(false)
                    val validator = DummyObjectValidatorBuilder(
                        key = DummyObjectValidatorBuilder.key<DummyObjectValidatorBuilder>(),
                        result = ReaderResult.Failure(location = LOCATION, error = MinPropertiesError)
                    )
                    val reader = reader<DTO> {
                        validation {
                            +validator
                        }
                        val name: ObjectProperty.Required<String> =
                            property(propertySpec(error = JsonErrors.PathMissing))
                        returns { _, location ->
                            DTO(name = +name).success(location)
                        }
                    }

                    "then all error should be returns" {
                        val input = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))
                        val result = reader.read(context = contextWithFailFastFalse, location = LOCATION, input)
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

            "the function of building the ResultBuilder type" - {
                val objectValuesMap: ObjectValuesMap = ObjectValuesMapInstance()

                "when the builder does not throw an exception" - {
                    val builder: (ObjectValuesMap.(ReaderContext, Location) -> ReaderResult<String>) = { _, location ->
                        ReaderResult.Success(location = location, value = USER_NAME)
                    }
                    val resultBuilder: ObjectReaderBuilder.ResultBuilder<String> = returns(builder)

                    "then call the builder should return a result" {
                        val result = resultBuilder.build(CONTEXT, LOCATION, objectValuesMap)
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when the builder does throw an exception" - {
                    val builder: (ObjectValuesMap.(ReaderContext, Location) -> ReaderResult<String>) =
                        { _, _ ->
                            throw IllegalStateException()
                        }
                    val resultBuilder: ObjectReaderBuilder.ResultBuilder<String> = returns(builder)

                    "when the context contains the exception handler" - {
                        val exceptionHandler: ExceptionsHandler = ExceptionsHandlerBuilder()
                            .apply {
                                this.exception<IllegalStateException> { _, _, _ ->
                                    JsonErrors.PathMissing
                                }
                            }
                            .build()
                        val contextWithExceptionHandler = CONTEXT + exceptionHandler

                        "then call the builder should return an error" {
                            val result = resultBuilder.build(contextWithExceptionHandler, LOCATION, objectValuesMap)
                            result as ReaderResult.Failure
                            result.causes shouldContainExactly listOf(
                                ReaderResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing)
                            )
                        }
                    }

                    "when the context does not contain the exception handler" - {

                        "then call the builder should re-throw exception" {
                            shouldThrow<IllegalStateException> {
                                resultBuilder.build(CONTEXT, LOCATION, objectValuesMap)
                            }
                        }
                    }
                }
            }

            "the StructNode#read extension-function" - {
                val input = ObjectNode(PROPERTY_NAME to StringNode(USER_NAME))

                "when property is the required" - {
                    val property: ObjectProperty = ObjectProperty.Required(
                        required(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME)
                        )
                    )

                    "then function should return a result" {
                        val result = input.read(CONTEXT, LOCATION, property)
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the defaultable" - {
                    val property: ObjectProperty = ObjectProperty.Defaultable(
                        defaultable(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME),
                            default = { DEFAULT_VALUE }
                        )
                    )

                    "then function should return a result" {
                        val result = input.read(CONTEXT, LOCATION, property)
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the optional" - {
                    val property: ObjectProperty = ObjectProperty.Optional(
                        optional(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME)
                        )
                    )

                    "then function should return a result" {
                        val result = input.read(CONTEXT, LOCATION, property)
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the optional with default" - {
                    val property: ObjectProperty = ObjectProperty.OptionalWithDefault(
                        optionalWithDefault(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME),
                            default = { DEFAULT_VALUE }
                        )
                    )

                    "then function should return a result" {
                        val result = input.read(CONTEXT, LOCATION, property)
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the nullable" - {
                    val property: ObjectProperty = ObjectProperty.Nullable(
                        nullable(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME)
                        )
                    )

                    "then function should return a result" {
                        val result = input.read(CONTEXT, LOCATION, property)
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }

                "when property is the nullable with default" - {
                    val property: ObjectProperty = ObjectProperty.NullableWithDefault(
                        nullableWithDefault(
                            name = PROPERTY_NAME,
                            reader = createReader(value = USER_NAME),
                            default = { DEFAULT_VALUE }
                        )
                    )

                    "then function should return a result" {
                        val result = input.read(CONTEXT, LOCATION, property)
                        result as ReaderResult.Success
                        result.value shouldBe USER_NAME
                    }
                }
            }
        }
    }

    fun <T : Any> propertySpec(value: T) = required(name = PROPERTY_NAME, reader = createReader(value = value))

    fun <T : Any> propertySpec(error: ReaderResult.Error) = required(
        name = PROPERTY_NAME,
        reader = DummyReader<T>(result = ReaderResult.Failure(location = LOCATION.append(PROPERTY_NAME), error = error))
    )

    fun <T : Any> createReader(value: T): DummyReader<T> =
        DummyReader(result = ReaderResult.Success(location = LOCATION.append(PROPERTY_NAME), value = value))

    internal data class DTO(val name: String)
}
