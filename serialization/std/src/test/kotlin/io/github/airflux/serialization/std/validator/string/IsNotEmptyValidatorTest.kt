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

package io.github.airflux.serialization.std.validator.string

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.kotest.assertions.shouldBeInvalid
import io.github.airflux.serialization.kotest.assertions.shouldBeValid
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec

internal class IsNotEmptyValidatorTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The string validator IsNotEmpty" - {
            val validator = StdStringValidator.isNotEmpty<EB, Unit>()

            "when the value is null" - {
                val str: String? = null

                "then the validator should not be applying" {
                    val result = validator.validate(ENV, LOCATION, str)
                    result.shouldBeValid()
                }
            }

            "when a string is empty" - {
                val str = ""

                "then the validator should return an error" {
                    val result = validator.validate(ENV, LOCATION, str)

                    result shouldBeInvalid JsValidatorResult.Invalid(
                        failure = JsReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    )
                }
            }

            "when a string is blank" - {
                val str = " "

                "then the validator should return the null value" {
                    val result = validator.validate(ENV, LOCATION, str)
                    result.shouldBeValid()
                }
            }

            "when a string is not blank" - {
                val str = "a"

                "then the validator should return the null value" {
                    val result = validator.validate(ENV, LOCATION, str)
                    result.shouldBeValid()
                }
            }
        }
    }

    internal class EB : IsNotEmptyStringValidator.ErrorBuilder {
        override fun isNotEmptyStringError(): JsReaderResult.Error =
            JsonErrors.Validation.Strings.IsEmpty
    }
}
