/*
 * Copyright 2021-2024 Maxim Sambulat.
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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validation.JsStructValidator
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.test.kotest.shouldBeInvalid
import io.github.airflux.serialization.test.kotest.shouldBeValid
import io.kotest.core.spec.style.FreeSpec

internal class MaxPropertiesStructValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "property-name"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "property-title"
        private const val MAX_PROPERTIES = 2
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
        private val PROPERTIES: StructProperties<EB, Unit> = emptyList()
    }

    init {

        "The struct validator MaxProperties" - {
            val validator: JsStructValidator<EB, Unit> = StdStructValidator.maxProperties(MAX_PROPERTIES)

            "when the struct is empty" - {
                val source = JsStruct()

                "then the validator should do not return any errors" {
                    val result = validator.validate(ENV, LOCATION, PROPERTIES, source)
                    result.shouldBeValid()
                }
            }

            "when the struct contains a number of properties less than the maximum" - {
                val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE))
                "then the validator should do not return any errors" {
                    val result = validator.validate(ENV, LOCATION, PROPERTIES, source)
                    result.shouldBeValid()
                }
            }

            "when the struct contains a number of properties equal to the maximum" - {
                val source = JsStruct(
                    ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE),
                    NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE)
                )

                "then the validator should do not return any errors" {
                    val result = validator.validate(ENV, LOCATION, PROPERTIES, source)
                    result.shouldBeValid()
                }
            }

            "when the struct contains a number of properties more than the maximum" - {
                val source = JsStruct(
                    ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE),
                    NAME_PROPERTY_NAME to JsString(NAME_PROPERTY_VALUE),
                    TITLE_PROPERTY_NAME to JsString(TITLE_PROPERTY_VALUE)
                )

                "then the validator should return an error" {
                    val result = validator.validate(ENV, LOCATION, PROPERTIES, source)

                    result shouldBeInvalid failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Struct.MaxProperties(expected = MAX_PROPERTIES, actual = 3)
                    )
                }
            }
        }
    }

    internal class EB : MaxPropertiesStructValidator.ErrorBuilder {
        override fun maxPropertiesStructError(expected: Int, actual: Int): JsReaderResult.Error =
            JsonErrors.Validation.Struct.MaxProperties(expected = expected, actual = actual)
    }
}
