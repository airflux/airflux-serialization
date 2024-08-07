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

package io.github.airflux.serialization.std.validator.number

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.kotest.assertions.shouldBeInvalid
import io.github.airflux.serialization.kotest.assertions.shouldBeValid
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec

internal class ExclusiveMaximumNumberValidatorTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
        private const val VALUE: Int = 2
    }

    init {

        "The numeric validator of the exclusive maximum allowed value" - {
            val validator: JsValidator<EB, Unit, Int> = StdNumberValidator.exclusiveMaximum(VALUE)

            "when a value is less than the allowed value" - {
                val value = VALUE - 1

                "then the validator should return the null value" {
                    val result = validator.validate(ENV, LOCATION, value)
                    result.shouldBeValid()
                }
            }

            "when a value is equal to the allowed value" - {
                val value = VALUE

                "then the validator should return an error" {
                    val result = validator.validate(ENV, LOCATION, value)

                    result shouldBeInvalid failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Numbers.Lt(expected = VALUE, actual = value)
                    )
                }
            }

            "when a value is greater than the allowed value" - {
                val value = VALUE + 1

                "then the validator should return an error" {
                    val result = validator.validate(ENV, LOCATION, value)

                    result shouldBeInvalid failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Numbers.Lt(expected = VALUE, actual = value)
                    )
                }
            }
        }
    }

    internal class EB : ExclusiveMaximumNumberValidator.ErrorBuilder {
        override fun exclusiveMaximumNumberError(expected: Number, actual: Number): JsReaderResult.Error =
            JsonErrors.Validation.Numbers.Lt(expected = expected, actual = actual)
    }
}
