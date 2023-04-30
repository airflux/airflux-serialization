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
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.validation.Validator
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.std.common.kotest.shouldBeInvalid
import io.github.airflux.serialization.std.common.kotest.shouldBeValid
import io.kotest.core.spec.style.FreeSpec

internal class MinimumNumberValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation
        private const val MIN_VALUE: Int = 2
    }

    init {

        "The numeric validator of the minimum allowed value" - {
            val validator: Validator<EB, Unit, Unit, Int> = StdNumberValidator.minimum(MIN_VALUE)

            "when a value is less than the min allowed" - {
                val value = MIN_VALUE - 1

                "then the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, value)

                    result shouldBeInvalid failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Numbers.Min(expected = MIN_VALUE, actual = value)
                    )
                }
            }

            "when a value is equal to the min allowed" - {
                val value = MIN_VALUE

                "then the validator should return the null value" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, value)
                    result.shouldBeValid()
                }
            }

            "when a value is more than the min allowed" - {
                val value = MIN_VALUE + 1

                "then the validator should return the null value" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, value)
                    result.shouldBeValid()
                }
            }
        }
    }

    internal class EB : MinimumNumberValidator.ErrorBuilder {
        override fun minimumNumberError(expected: Number, actual: Number): ReadingResult.Error =
            JsonErrors.Validation.Numbers.Min(expected = expected, actual = actual)
    }
}
