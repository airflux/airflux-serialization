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

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validation.Validated
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class MinLengthValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private const val MIN_VALUE: Int = 2
    }

    init {

        "The string validator MinLength" - {
            val validator = StdStringValidator.minLength<EB, Unit, Unit>(MIN_VALUE)

            "when the value is null" - {
                val str: String? = null

                "then the validator should not be applying" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, str)
                    result shouldBe valid()
                }
            }

            "when a string is empty" - {
                val str = ""

                "then the validator should return an error" {
                    val result = validator.validate(ENV, CONTEXT, LOCATION, str)

                    val failure = result.shouldBeInstanceOf<Validated.Invalid>()
                    failure.reason shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Strings.MinLength(
                            expected = MIN_VALUE,
                            actual = str.length
                        )
                    )
                }
            }

            "when a string is blank" - {

                "when the length of the string is less the min allowed length" - {
                    val str = " "

                    "then the validator should return an error" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, str)

                        val failure = result.shouldBeInstanceOf<Validated.Invalid>()
                        failure.reason shouldBe ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.MinLength(
                                expected = MIN_VALUE,
                                actual = str.length
                            )
                        )
                    }
                }

                "when the length of the string is equal to the min allowed length" - {
                    val str = "  "

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, str)
                        result shouldBe valid()
                    }
                }

                "when the length of the string is more the min allowed length" - {
                    val str = "   "

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, str)
                        result shouldBe valid()
                    }
                }
            }

            "when a string is not blank" - {

                "when the length of the string is less the min allowed length" - {
                    val str = "a"

                    "then the validator should return an error" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, str)

                        val failure = result.shouldBeInstanceOf<Validated.Invalid>()
                        failure.reason shouldBe ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.MinLength(
                                expected = MIN_VALUE,
                                actual = str.length
                            )
                        )
                    }
                }

                "when the length of the string is equal to the min allowed length" - {
                    val str = "ab"

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, str)
                        result shouldBe valid()
                    }
                }

                "when the length of the string is more the min allowed length" - {
                    val str = "abc"

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, str)
                        result shouldBe valid()
                    }
                }
            }
        }
    }

    internal class EB : MinLengthStringValidator.ErrorBuilder {
        override fun minLengthStringError(expected: Int, actual: Int): ReaderResult.Error =
            JsonErrors.Validation.Strings.MinLength(expected = expected, actual = actual)
    }
}
