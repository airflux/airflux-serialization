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

package io.github.airflux.serialization.std.validator.comparable

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validator.Validator
import io.github.airflux.serialization.std.validator.comparison.MinComparisonValidator
import io.github.airflux.serialization.std.validator.comparison.StdComparisonValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MinComparisonValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty
        private const val MIN_VALUE: Int = 2
    }

    init {

        "The string validator Min" - {
            val validator: Validator<EB, Unit, Int> = StdComparisonValidator.min(MIN_VALUE)

            "when a value is less than the min allowed" - {
                val value = MIN_VALUE - 1

                "then the validator should return an error" {
                    val failure = validator.validate(ENV, LOCATION, value)

                    failure.shouldNotBeNull()
                    failure shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Numbers.Min(expected = MIN_VALUE, actual = value)
                    )
                }
            }

            "when a value is equal to the min allowed" - {
                val value = MIN_VALUE

                "then the validator should return the null value" {
                    val errors = validator.validate(ENV, LOCATION, value)
                    errors.shouldBeNull()
                }
            }

            "when a value is more than the min allowed" - {
                val value = MIN_VALUE + 1

                "then the validator should return the null value" {
                    val errors = validator.validate(ENV, LOCATION, value)
                    errors.shouldBeNull()
                }
            }
        }
    }

    internal class EB : MinComparisonValidator.ErrorBuilder {
        override fun minComparisonError(expected: Number, actual: Number): ReaderResult.Error =
            JsonErrors.Validation.Numbers.Min(expected = expected, actual = actual)
    }
}
