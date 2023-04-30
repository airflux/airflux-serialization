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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.std.common.kotest.shouldBeInvalid
import io.github.airflux.serialization.std.common.kotest.shouldBeValid
import io.kotest.core.spec.style.FreeSpec

internal class PatternValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation
        private val PATTERN: Regex = "\\d+".toRegex()
    }

    init {

        "The string validator Pattern" - {
            val validator = StdStringValidator.pattern<EB, Unit, Unit>(PATTERN)

            "when the value is null" - {
                val str: String? = null

                "then the validator should not be applying" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, str)

                    result.shouldBeValid()
                }
            }

            "when the value is empty" - {
                val str = ""

                "then the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, str)

                    result shouldBeInvalid failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Strings.Pattern(value = str, regex = PATTERN)
                    )
                }
            }

            "when the value is blank" - {
                val str = " "

                "then the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, str)

                    result shouldBeInvalid failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Strings.Pattern(value = str, regex = PATTERN)
                    )
                }
            }

            "when the value is not blank" - {

                "when the string is not matching to the pattern" - {
                    val str = "a"

                    "then the validator should return an error" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, str)

                        result shouldBeInvalid failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.Pattern(value = str, regex = PATTERN)
                        )
                    }
                }

                "when the string is matching to the pattern" - {
                    val str = "123"

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, str)
                        result.shouldBeValid()
                    }
                }
            }
        }
    }

    internal class EB : PatternStringValidator.ErrorBuilder {
        override fun patternStringError(value: String, pattern: Regex): ReadingResult.Error =
            JsonErrors.Validation.Strings.Pattern(value = value, regex = pattern)
    }
}
