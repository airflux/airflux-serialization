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

package io.github.airflux.serialization.std.validator.`object`

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.option.FailFast
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.ObjectProperty
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.`object`.builder.validator.ObjectValidator
import io.github.airflux.serialization.std.reader.StringReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class AdditionalPropertiesObjectValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "property-name"
        private const val NAME_PROPERTY_NAME = "title"
        private const val NAME_PROPERTY_VALUE = "property-title"
        private val LOCATION = Location.empty

        private val idProperty: ObjectProperty.Required<String> =
            ObjectProperty.Required(required(ID_PROPERTY_NAME, StringReader))

        val properties: ObjectProperties = ObjectProperties(listOf(idProperty))
    }

    init {

        "The object validator AdditionalProperties" - {
            val validator: ObjectValidator = StdObjectValidator.additionalProperties.build(properties)

            "when the reader context does not contain the error builder" - {
                val context = ReaderContext()
                val input = StructNode(
                    ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                    TITLE_PROPERTY_VALUE to StringNode(TITLE_PROPERTY_NAME)
                )

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validate(context, LOCATION, properties, input)
                    }
                    exception.message shouldBe "The error builder '${AdditionalPropertiesObjectValidator.ErrorBuilder.errorBuilderName()}' is missing in the context."
                }
            }

            "when the reader context contains the error builder" - {
                val context = ReaderContext(
                    AdditionalPropertiesObjectValidator.ErrorBuilder { JsonErrors.Validation.Object.AdditionalProperties }
                )

                "when the object is empty" - {
                    val input = StructNode()

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(context, LOCATION, properties, input)
                        errors.shouldBeNull()
                    }
                }

                "when the object does not contains additional properties" - {
                    val input = StructNode(ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(context, LOCATION, properties, input)
                        errors.shouldBeNull()
                    }
                }

                "when the object contains additional properties" - {
                    val input = StructNode(
                        ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                        TITLE_PROPERTY_VALUE to StringNode(TITLE_PROPERTY_NAME),
                        NAME_PROPERTY_VALUE to StringNode(NAME_PROPERTY_NAME)
                    )

                    "when fail-fast is missing" - {

                        "then the validator should return an error" {
                            val failure = validator.validate(context, LOCATION, properties, input)

                            failure.shouldNotBeNull()
                            failure shouldBe ReaderResult.Failure(
                                location = LOCATION.append(TITLE_PROPERTY_VALUE),
                                error = JsonErrors.Validation.Object.AdditionalProperties
                            )
                        }
                    }

                    "when fail-fast is true" - {
                        val contextWithFailFast = context + FailFast(true)

                        "then the validator should return an error" {
                            val failure = validator.validate(contextWithFailFast, LOCATION, properties, input)

                            failure.shouldNotBeNull()
                            failure shouldBe ReaderResult.Failure(
                                location = LOCATION.append(TITLE_PROPERTY_VALUE),
                                error = JsonErrors.Validation.Object.AdditionalProperties
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val contextWithFailFast = context + FailFast(false)

                        "then the validator should return an error" {
                            val failure = validator.validate(contextWithFailFast, LOCATION, properties, input)

                            failure.shouldNotBeNull()
                            failure shouldBe listOf(
                                ReaderResult.Failure(
                                    location = LOCATION.append(TITLE_PROPERTY_VALUE),
                                    error = JsonErrors.Validation.Object.AdditionalProperties
                                ),
                                ReaderResult.Failure(
                                    location = LOCATION.append(NAME_PROPERTY_VALUE),
                                    error = JsonErrors.Validation.Object.AdditionalProperties
                                )
                            ).merge()
                        }
                    }
                }
            }
        }
    }
}
