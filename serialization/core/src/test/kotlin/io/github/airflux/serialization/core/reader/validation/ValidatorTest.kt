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

package io.github.airflux.serialization.core.reader.validation

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.ReadingResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.failure
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class ValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(Unit, Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
    }

    init {

        "The Validator type" - {

            "testing the or composite operator" - {

                "if the left validator returns success then the right validator doesn't execute" {
                    val leftValidator = Validator<Unit, Unit, Unit, Unit> { _, _, _, _ -> valid() }

                    val rightValidator = Validator<Unit, Unit, Unit, Unit> { _, _, location, _ ->
                        invalid(location, ValidationErrors.PathMissing)
                    }

                    val composeValidator = leftValidator or rightValidator
                    val result = composeValidator.validate(ENV, CONTEXT, LOCATION, Unit)

                    result shouldBe valid()
                }

                "if the left validator returns an error" - {
                    val leftValidator = Validator<Unit, Unit, Unit, Unit> { _, _, location, _ ->
                        invalid(location, ValidationErrors.PathMissing)
                    }

                    "and the right validator returns success then returning the first error" {
                        val rightValidator = Validator<Unit, Unit, Unit, Unit> { _, _, _, _ -> valid() }

                        val composeValidator = leftValidator or rightValidator
                        val result = composeValidator.validate(ENV, CONTEXT, LOCATION, Unit)

                        result shouldBe valid()
                    }

                    "and the right validator returns an error then returning both errors" {
                        val rightValidator = Validator<Unit, Unit, Unit, Unit> { _, _, location, _ ->
                            invalid(location, ValidationErrors.InvalidType)
                        }

                        val composeValidator = leftValidator or rightValidator
                        val result = composeValidator.validate(ENV, CONTEXT, LOCATION, Unit)

                        val failure = result.shouldBeInstanceOf<ValidationResult.Invalid>()
                        failure.reason shouldBe listOf(
                            ReadingResult.Failure(LOCATION, ValidationErrors.PathMissing),
                            ReadingResult.Failure(LOCATION, ValidationErrors.InvalidType)
                        ).merge()
                    }
                }
            }

            "testing the and composite operator" - {

                "if the left validator returns an error then the right validator doesn't execute" {
                    val leftValidator = Validator<Unit, Unit, Unit, Unit> { _, _, location, _ ->
                        invalid(location, ValidationErrors.PathMissing)
                    }

                    val rightValidator = Validator<Unit, Unit, Unit, Unit> { _, _, location, _ ->
                        invalid(location, ValidationErrors.InvalidType)
                    }

                    val composeValidator = leftValidator and rightValidator
                    val result = composeValidator.validate(ENV, CONTEXT, LOCATION, Unit)

                    val failure = result.shouldBeInstanceOf<ValidationResult.Invalid>()
                    failure.reason shouldBe failure(LOCATION, ValidationErrors.PathMissing)
                }

                "if the left validator returns a success" - {
                    val leftValidator = Validator<Unit, Unit, Unit, Unit> { _, _, _, _ -> valid() }

                    "and the second validator returns success, then success is returned" {
                        val rightValidator = Validator<Unit, Unit, Unit, Unit> { _, _, _, _ -> valid() }

                        val composeValidator = leftValidator and rightValidator
                        val result = composeValidator.validate(ENV, CONTEXT, LOCATION, Unit)

                        result shouldBe valid()
                    }

                    "and the right validator returns an error, then an error is returned" {
                        val rightValidator = Validator<Unit, Unit, Unit, Unit> { _, _, location, _ ->
                            invalid(location, ValidationErrors.PathMissing)
                        }

                        val composeValidator = leftValidator and rightValidator
                        val result = composeValidator.validate(ENV, CONTEXT, LOCATION, Unit)

                        val failure = result.shouldBeInstanceOf<ValidationResult.Invalid>()
                            failure.reason shouldBe failure(LOCATION, ValidationErrors.PathMissing)
                    }
                }
            }
        }
    }

    private sealed class ValidationErrors : ReadingResult.Error {
        object PathMissing : ValidationErrors()
        object InvalidType : ValidationErrors()
    }
}
