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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.test.kotest.shouldBeInvalid
import io.github.airflux.serialization.test.kotest.shouldBeValid
import io.kotest.core.spec.style.FreeSpec

internal class JsValidatorTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(Unit, Unit)
        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The JsValidator type" - {

            "testing the or composite operator" - {

                "if the left validator returns success then the right validator doesn't execute" {
                    val leftValidator = JsValidator<Unit, Unit, Unit> { _, _, _ -> valid() }

                    val rightValidator = JsValidator<Unit, Unit, Unit> { _, location, _ ->
                        invalid(location, ValidationErrors.PathMissing)
                    }

                    val composeValidator = leftValidator or rightValidator
                    val result = composeValidator.validate(ENV, LOCATION, Unit)

                    result.shouldBeValid()
                }

                "if the left validator returns an error" - {
                    val leftValidator = JsValidator<Unit, Unit, Unit> { _, location, _ ->
                        invalid(location, ValidationErrors.PathMissing)
                    }

                    "and the right validator returns success then returning the first error" {
                        val rightValidator = JsValidator<Unit, Unit, Unit> { _, _, _ -> valid() }

                        val composeValidator = leftValidator or rightValidator
                        val result = composeValidator.validate(ENV, LOCATION, Unit)

                        result.shouldBeValid()
                    }

                    "and the right validator returns an error then returning both errors" {
                        val rightValidator = JsValidator<Unit, Unit, Unit> { _, location, _ ->
                            invalid(location, ValidationErrors.InvalidType)
                        }

                        val composeValidator = leftValidator or rightValidator
                        val result = composeValidator.validate(ENV, LOCATION, Unit)

                        result shouldBeInvalid
                            JsReaderResult.Failure(LOCATION, ValidationErrors.PathMissing) +
                            JsReaderResult.Failure(LOCATION, ValidationErrors.InvalidType)
                    }
                }
            }

            "testing the and composite operator" - {

                "if the left validator returns an error then the right validator doesn't execute" {
                    val leftValidator = JsValidator<Unit, Unit, Unit> { _, location, _ ->
                        invalid(location, ValidationErrors.PathMissing)
                    }

                    val rightValidator = JsValidator<Unit, Unit, Unit> { _, location, _ ->
                        invalid(location, ValidationErrors.InvalidType)
                    }

                    val composeValidator = leftValidator and rightValidator
                    val result = composeValidator.validate(ENV, LOCATION, Unit)

                    result shouldBeInvalid failure(LOCATION, ValidationErrors.PathMissing)
                }

                "if the left validator returns a success" - {
                    val leftValidator = JsValidator<Unit, Unit, Unit> { _, _, _ -> valid() }

                    "and the second validator returns success, then success is returned" {
                        val rightValidator = JsValidator<Unit, Unit, Unit> { _, _, _ -> valid() }

                        val composeValidator = leftValidator and rightValidator
                        val result = composeValidator.validate(ENV, LOCATION, Unit)

                        result.shouldBeValid()
                    }

                    "and the right validator returns an error, then an error is returned" {
                        val rightValidator = JsValidator<Unit, Unit, Unit> { _, location, _ ->
                            invalid(location, ValidationErrors.PathMissing)
                        }

                        val composeValidator = leftValidator and rightValidator
                        val result = composeValidator.validate(ENV, LOCATION, Unit)

                        result shouldBeInvalid failure(LOCATION, ValidationErrors.PathMissing)
                    }
                }
            }
        }
    }

    private sealed class ValidationErrors : JsReaderResult.Error {
        object PathMissing : ValidationErrors()
        object InvalidType : ValidationErrors()
    }
}
