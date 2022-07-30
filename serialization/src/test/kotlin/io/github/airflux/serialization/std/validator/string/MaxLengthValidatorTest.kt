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

package io.github.airflux.serialization.std.validator.string

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validator.Validator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MaxLengthValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: Location = Location.empty
        private const val MAX_VALUE: Int = 2
    }

    init {

        "The string validator MaxLength" - {
            val validator: Validator<String> = StdStringValidator.maxLength(MAX_VALUE)

            "when the reader context does not contain the error builder" - {
                val context = ReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validate(context, LOCATION, "abc")
                    }
                    exception.message shouldBe "The error builder '${MaxLengthStringValidator.ErrorBuilder.errorBuilderName()}' is missing in the context."
                }
            }

            "when the reader context contains the error builder" - {
                val context = ReaderContext(
                    MaxLengthStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::MaxLength)
                )

                "when a string is empty" - {
                    val str = ""

                    "then the validator should return the null value" {
                        val errors = validator.validate(context, LOCATION, str)
                        errors.shouldBeNull()
                    }
                }

                "when a string is blank" - {

                    "when the length of the string is less the max allowed length" - {
                        val str = " "

                        "then the validator should return the null value" {
                            val errors = validator.validate(context, LOCATION, str)
                            errors.shouldBeNull()
                        }
                    }

                    "when the length of the string is equal to the max allowed length" - {
                        val str = "  "

                        "then the validator should return the null value" {
                            val errors = validator.validate(context, LOCATION, str)
                            errors.shouldBeNull()
                        }
                    }

                    "when the length of the string is more the max allowed length" - {
                        val str = "   "

                        "then the validator should return an error" {
                            val failure = validator.validate(context, LOCATION, str)

                            failure.shouldNotBeNull()
                            failure shouldBe ReaderResult.Failure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.MaxLength(
                                    expected = MAX_VALUE,
                                    actual = str.length
                                )
                            )
                        }
                    }
                }

                "when a string is not blank" - {

                    "when the length of the string is less the max allowed length" - {
                        val str = "a"

                        "then the validator should return the null value" {
                            val errors = validator.validate(context, LOCATION, str)
                            errors.shouldBeNull()
                        }
                    }

                    "when the length of the string is equal to the max allowed length" - {
                        val str = "ab"

                        "then the validator should return the null value" {
                            val errors = validator.validate(context, LOCATION, str)
                            errors.shouldBeNull()
                        }
                    }

                    "when the length of the string is more the max allowed length" - {
                        val str = "abc"

                        "then the validator should return an error" {
                            val failure = validator.validate(context, LOCATION, str)

                            failure.shouldNotBeNull()
                            failure shouldBe ReaderResult.Failure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.MaxLength(
                                    expected = MAX_VALUE,
                                    actual = str.length
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
