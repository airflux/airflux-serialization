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

package io.github.airflux.serialization.std.validator.string

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validator.Validator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class IsAValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val PATTERN: Regex = "\\d+".toRegex()
        private val IS_DIGITAL: (String) -> Boolean = { value: String -> PATTERN.matches(value) }
    }

    init {

        "The string validator MinLength" - {
            val validator: Validator<EB, Unit, Unit, String> = StdStringValidator.isA(IS_DIGITAL)

            "when a string is empty" - {
                val str = ""

                "then the validator should return an error" {
                    val failure = validator.validate(ENV, CONTEXT, LOCATION, str)

                    failure.shouldNotBeNull()
                    failure shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Strings.IsA(value = str)
                    )
                }
            }

            "when a string is blank" - {
                val str = " "

                "then the validator should return an error" {
                    val failure = validator.validate(ENV, CONTEXT, LOCATION, str)

                    failure.shouldNotBeNull()
                    failure shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Strings.IsA(value = str)
                    )
                }
            }

            "when the string is not a digital" - {
                val str = "a"

                "then the validator should return an error" {
                    val failure = validator.validate(ENV, CONTEXT, LOCATION, str)

                    failure.shouldNotBeNull()
                    failure shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Strings.IsA(value = str)
                    )
                }
            }

            "when the string is a digital" - {
                val str = "123"

                "then the validator should return the null value" {
                    val errors = validator.validate(ENV, CONTEXT, LOCATION, str)
                    errors.shouldBeNull()
                }
            }
        }
    }

    internal class EB : IsAStringValidator.ErrorBuilder {
        override fun isAStringError(value: String): ReaderResult.Error =
            JsonErrors.Validation.Strings.IsA(value)
    }
}
