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

package io.github.airflux.serialization.std.validator.struct

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.dummyStringReader
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.value.ObjectNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperty
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectValidator
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
        private val StringReader = dummyStringReader<EB, CTX>()
        private val LOCATION = Location.empty
        private val idProperty: ObjectProperty.Required<EB, CTX, String> =
            ObjectProperty.Required(required(ID_PROPERTY_NAME, StringReader))
        private val PROPERTIES: ObjectProperties<EB, CTX> = ObjectProperties(listOf(idProperty))
    }

    init {

        "The object validator AdditionalProperties" - {
            val validator: ObjectValidator<EB, CTX> =
                StdObjectValidator.additionalProperties<EB, CTX>().build(PROPERTIES)

            "when the object is empty" - {
                val source = ObjectNode()

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(envWithFailFastIsTrue, LOCATION, PROPERTIES, source)
                        errors.shouldBeNull()
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), CTX(failFast = false))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(envWithFailFastIsFalse, LOCATION, PROPERTIES, source)
                        errors.shouldBeNull()
                    }
                }
            }

            "when the object does not contains additional properties" - {
                val source = ObjectNode(ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE))

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(envWithFailFastIsTrue, LOCATION, PROPERTIES, source)
                        errors.shouldBeNull()
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), CTX(failFast = false))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(envWithFailFastIsFalse, LOCATION, PROPERTIES, source)
                        errors.shouldBeNull()
                    }
                }
            }

            "when the object contains additional properties" - {
                val source = ObjectNode(
                    ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                    TITLE_PROPERTY_VALUE to StringNode(TITLE_PROPERTY_NAME),
                    NAME_PROPERTY_VALUE to StringNode(NAME_PROPERTY_NAME)
                )

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), CTX(failFast = true))

                    "then the validator should return first error" {
                        val failure = validator.validate(envWithFailFastIsTrue, LOCATION, PROPERTIES, source)

                        failure.shouldNotBeNull()
                        failure shouldBe ReaderResult.Failure(
                            location = LOCATION.append(TITLE_PROPERTY_VALUE),
                            error = JsonErrors.Validation.Object.AdditionalProperties
                        )
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), CTX(failFast = false))

                    "then the validator should return all errors" {
                        val failure = validator.validate(envWithFailFastIsFalse, LOCATION, PROPERTIES, source)

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

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder,
                        AdditionalPropertiesObjectValidator.ErrorBuilder {

        override fun additionalPropertiesObjectError(): ReaderResult.Error =
            JsonErrors.Validation.Object.AdditionalProperties

        override fun invalidTypeError(expected: ValueNode.Type, actual: ValueNode.Type): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing
    }

    internal class CTX(override val failFast: Boolean) : FailFastOption
}
