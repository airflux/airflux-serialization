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

package io.github.airflux.serialization.std.validator.struct

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidator
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class MinPropertiesStructValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "property-name"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "property-title"
        private const val MIN_PROPERTIES = 2
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val PROPERTIES: StructProperties<EB, Unit, Unit> = emptyList()
    }

    init {

        "The struct validator MinProperties" - {
            val validator: StructValidator<EB, Unit, Unit> =
                StdStructValidator.minProperties<EB, Unit, Unit>(MIN_PROPERTIES).build(PROPERTIES)

            "when the struct is empty" - {
                val source = StructNode()

                "then the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, PROPERTIES, source)

                    val failure = result.shouldBeInstanceOf<ValidationResult.Invalid>()
                    failure.reason shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Struct.MinProperties(expected = MIN_PROPERTIES, actual = 0)
                    )
                }
            }

            "when the struct contains a number of properties less than the minimum" - {
                val source = StructNode(ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE))

                "then the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, PROPERTIES, source)

                    val failure = result.shouldBeInstanceOf<ValidationResult.Invalid>()
                    failure.reason shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Struct.MinProperties(expected = MIN_PROPERTIES, actual = 1)
                    )
                }
            }

            "when the struct contains a number of properties equal to the minimum" - {
                val source = StructNode(
                    ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                    NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE)
                )

                "then the validator should do not return any errors" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, PROPERTIES, source)
                    result shouldBe valid()
                }
            }

            "when the struct contains a number of properties more than the minimum" - {
                val source = StructNode(
                    ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                    NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                    TITLE_PROPERTY_NAME to StringNode(TITLE_PROPERTY_VALUE)
                )

                "then the validator should do not return any errors" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, PROPERTIES, source)
                    result shouldBe valid()
                }
            }
        }
    }

    internal class EB : MinPropertiesStructValidator.ErrorBuilder {
        override fun minPropertiesStructError(expected: Int, actual: Int): ReaderResult.Error =
            JsonErrors.Validation.Struct.MinProperties(expected = expected, actual = actual)
    }
}
