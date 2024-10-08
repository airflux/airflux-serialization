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
import io.github.airflux.serialization.core.reader.struct.property.JsStructProperties
import io.github.airflux.serialization.core.reader.struct.validation.JsStructValidator
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.kotest.assertions.shouldBeInvalid
import io.github.airflux.serialization.kotest.assertions.shouldBeValid
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec

internal class IsNotEmptyStructValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private val ENV = JsReaderEnv(config = JsReaderEnv.Config(EB(), Unit))
        private val LOCATION: JsLocation = JsLocation
        private val PROPERTIES: JsStructProperties<EB, Unit> = emptyList()
    }

    init {

        "The struct validator IsNotEmpty" - {
            val validator: JsStructValidator<EB, Unit> = StdStructValidator.isNotEmpty()

            "when the struct is empty" - {
                val source = JsStruct()

                "then the validator should return an error" {
                    val result = validator.validate(ENV, LOCATION, PROPERTIES, source)

                    result shouldBeInvalid JsValidatorResult.Invalid(
                        failure = JsReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Struct.IsEmpty
                        )
                    )
                }
            }

            "when the struct is not empty" - {
                val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE))

                "then the validator should do not return any errors" {
                    val result = validator.validate(ENV, LOCATION, PROPERTIES, source)
                    result.shouldBeValid()
                }
            }
        }
    }

    internal class EB : IsNotEmptyStructValidator.ErrorBuilder {
        override fun isNotEmptyStructError(): JsReaderResult.Error =
            JsonErrors.Validation.Struct.IsEmpty
    }
}
