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
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class AdditionalPropertiesStructValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "property-name"
        private const val NAME_PROPERTY_NAME = "title"
        private const val NAME_PROPERTY_VALUE = "property-title"
        private val StringReader = dummyStringReader<EB, OPTS, Unit>()
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val idProperty: StructProperty.NonNullable<EB, OPTS, Unit, String> =
            StructProperty.NonNullable(required(ID_PROPERTY_NAME, StringReader))
        private val PROPERTIES = listOf(idProperty)
    }

    init {

        "The struct validator AdditionalProperties" - {
            val validator: StructValidator<EB, OPTS, Unit> =
                StdStructValidator.additionalProperties<EB, OPTS, Unit>().build(PROPERTIES)

            "when the struct is empty" - {
                val source = StructNode()

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(envWithFailFastIsTrue, CONTEXT, LOCATION, PROPERTIES, source)
                        errors.shouldBeNull()
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(envWithFailFastIsFalse, CONTEXT, LOCATION, PROPERTIES, source)
                        errors.shouldBeNull()
                    }
                }
            }

            "when the struct does not contains additional properties" - {
                val source = StructNode(ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE))

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(envWithFailFastIsTrue, CONTEXT, LOCATION, PROPERTIES, source)
                        errors.shouldBeNull()
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(envWithFailFastIsFalse, CONTEXT, LOCATION, PROPERTIES, source)
                        errors.shouldBeNull()
                    }
                }
            }

            "when the struct contains additional properties" - {
                val source = StructNode(
                    ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                    TITLE_PROPERTY_VALUE to StringNode(TITLE_PROPERTY_NAME),
                    NAME_PROPERTY_VALUE to StringNode(NAME_PROPERTY_NAME)
                )

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                    "then the validator should return first error" {
                        val failure = validator.validate(envWithFailFastIsTrue, CONTEXT, LOCATION, PROPERTIES, source)

                        failure.shouldNotBeNull()
                        failure shouldBe ReaderResult.Failure(
                            location = LOCATION.append(TITLE_PROPERTY_VALUE),
                            error = JsonErrors.Validation.Struct.AdditionalProperties
                        )
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                    "then the validator should return all errors" {
                        val failure = validator.validate(envWithFailFastIsFalse, CONTEXT, LOCATION, PROPERTIES, source)

                        failure.shouldNotBeNull()
                        failure shouldBe listOf(
                            ReaderResult.Failure(
                                location = LOCATION.append(TITLE_PROPERTY_VALUE),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            ),
                            ReaderResult.Failure(
                                location = LOCATION.append(NAME_PROPERTY_VALUE),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        ).merge()
                    }
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder,
                        AdditionalPropertiesStructValidator.ErrorBuilder {

        override fun additionalPropertiesStructError(): ReaderResult.Error =
            JsonErrors.Validation.Struct.AdditionalProperties

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
