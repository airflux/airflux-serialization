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

package io.github.airflux.serialization.std.validator.number

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.std.common.kotest.shouldBeInvalid
import io.github.airflux.serialization.std.common.kotest.shouldBeValid
import io.kotest.core.spec.style.FreeSpec

internal class ExclusiveMinimumNumberValidatorTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation
        private const val VALUE: Int = 2
    }

    init {

        "The numeric validator of the exclusive minimum allowed value" - {
            val validator: JsValidator<EB, Unit, Unit, Int> = StdNumberValidator.exclusiveMinimum(VALUE)

            "when a value is less than the allowed value" - {
                val value = VALUE - 1

                "then the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, value)

                    result shouldBeInvalid failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Numbers.Gt(expected = VALUE, actual = value)
                    )
                }
            }

            "when a value is equal to the allowed value" - {
                val value = VALUE

                "then the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, value)

                    result shouldBeInvalid failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Numbers.Gt(expected = VALUE, actual = value)
                    )
                }
            }

            "when a value is greater than the allowed value" - {
                val value = VALUE + 1

                "then the validator should return the null value" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, value)
                    result.shouldBeValid()
                }
            }
        }
    }

    internal class EB : ExclusiveMinimumNumberValidator.ErrorBuilder {
        override fun exclusiveMinimumNumberError(expected: Number, actual: Number): ReadingResult.Error =
            JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual)
    }
}
