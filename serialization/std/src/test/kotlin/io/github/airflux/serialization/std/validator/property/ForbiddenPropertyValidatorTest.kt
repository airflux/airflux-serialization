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

package io.github.airflux.serialization.std.validator.property

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.reader.validation.Validator
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class ForbiddenPropertyValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private const val VALUE: Int = 2
    }

    init {

        "The property value validator the Mandatory" - {

            "when the predicate returns the true value" - {
                val validator: Validator<EB, Unit, Unit, Int?> = StdPropertyValidator.forbidden { _, _, _ -> true }

                "when a value is missing" - {
                    val value: Int? = null

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, value)
                        result shouldBe valid()
                    }
                }

                "when a value is present" - {
                    val value = VALUE

                    "then the validator should return an error" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, value)

                        val failure = result.shouldBeInstanceOf<ValidationResult.Invalid>()
                        failure.reason shouldBe failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Struct.ForbiddenProperty
                        )
                    }
                }
            }

            "when the predicate returns the false value" - {
                val validator: Validator<EB, Unit, Unit, Int?> = StdPropertyValidator.forbidden { _, _, _ -> false }

                "when a value is missing" - {
                    val value: Int? = null

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, value)
                        result shouldBe valid()
                    }
                }

                "when a value is present" - {
                    val value = VALUE

                    "then the validator should return the null value" {
                        val result = validator.validate(ENV, CONTEXT, LOCATION, value)
                        result shouldBe valid()
                    }
                }
            }
        }
    }

    internal class EB : ForbiddenPropertyValidator.ErrorBuilder {
        override fun forbiddenPropertyError(): ReadingResult.Error = JsonErrors.Validation.Struct.ForbiddenProperty
    }
}
