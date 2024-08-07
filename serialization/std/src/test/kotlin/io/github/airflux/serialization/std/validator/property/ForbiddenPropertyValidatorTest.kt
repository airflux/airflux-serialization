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

package io.github.airflux.serialization.std.validator.property

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.kotest.assertions.shouldBeInvalid
import io.github.airflux.serialization.kotest.assertions.shouldBeValid
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec

internal class ForbiddenPropertyValidatorTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
        private const val VALUE: Int = 2
    }

    init {

        "The property value validator the Mandatory" - {

            "when the predicate returns the true value" - {
                val validator: JsValidator<EB, Unit, Int?> = StdPropertyValidator.forbidden { _, _ -> true }

                "when a value is missing" - {
                    val value: Int? = null

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, LOCATION, value)
                        result.shouldBeValid()
                    }
                }

                "when a value is present" - {
                    val value = VALUE

                    "then the validator should return an error" {
                        val result = validator.validate(ENV, LOCATION, value)

                        result shouldBeInvalid failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Struct.ForbiddenProperty
                        )
                    }
                }
            }

            "when the predicate returns the false value" - {
                val validator: JsValidator<EB, Unit, Int?> = StdPropertyValidator.forbidden { _, _ -> false }

                "when a value is missing" - {
                    val value: Int? = null

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, LOCATION, value)
                        result.shouldBeValid()
                    }
                }

                "when a value is present" - {
                    val value = VALUE

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, LOCATION, value)
                        result.shouldBeValid()
                    }
                }
            }
        }
    }

    internal class EB : ForbiddenPropertyValidator.ErrorBuilder {
        override fun forbiddenPropertyError(): JsReaderResult.Error = JsonErrors.Validation.Struct.ForbiddenProperty
    }
}
