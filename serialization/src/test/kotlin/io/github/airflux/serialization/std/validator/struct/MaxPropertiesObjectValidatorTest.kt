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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ObjectNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MaxPropertiesObjectValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "property-name"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "property-title"
        private const val MAX_PROPERTIES = 2
        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty
        private val PROPERTIES: ObjectProperties<EB, Unit> = ObjectProperties(emptyList())
    }

    init {

        "The object validator MaxProperties" - {
            val validator: ObjectValidator<EB, Unit> =
                StdObjectValidator.maxProperties<EB, Unit>(MAX_PROPERTIES).build(PROPERTIES)

            "when the object is empty" - {
                val source = ObjectNode()

                "then the validator should do not return any errors" {
                    val errors = validator.validate(ENV, LOCATION, PROPERTIES, source)
                    errors.shouldBeNull()
                }
            }

            "when the object contains a number of properties less than the maximum" - {
                val source = ObjectNode(ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE))
                "then the validator should do not return any errors" {
                    val errors = validator.validate(ENV, LOCATION, PROPERTIES, source)
                    errors.shouldBeNull()
                }
            }

            "when the object contains a number of properties equal to the maximum" - {
                val source = ObjectNode(
                    ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                    NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE)
                )

                "then the validator should do not return any errors" {
                    val errors = validator.validate(ENV, LOCATION, PROPERTIES, source)
                    errors.shouldBeNull()
                }
            }

            "when the object contains a number of properties more than the maximum" - {
                val source = ObjectNode(
                    ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                    NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                    TITLE_PROPERTY_NAME to StringNode(TITLE_PROPERTY_VALUE)
                )

                "then the validator should return an error" {
                    val failure = validator.validate(ENV, LOCATION, PROPERTIES, source)
                    failure.shouldNotBeNull()
                    failure shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Object.MaxProperties(expected = MAX_PROPERTIES, actual = 3)
                    )
                }
            }
        }
    }

    internal class EB : MaxPropertiesObjectValidator.ErrorBuilder {
        override fun maxPropertiesObjectError(expected: Int, actual: Int): ReaderResult.Error =
            JsonErrors.Validation.Object.MaxProperties(expected = expected, actual = actual)
    }
}
