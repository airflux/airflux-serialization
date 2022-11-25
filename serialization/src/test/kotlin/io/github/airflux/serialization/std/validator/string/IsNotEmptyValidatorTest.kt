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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validator.Validator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class IsNotEmptyValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty
    }

    init {

        "The string validator IsNotEmpty" - {
            val validator: Validator<EB, Unit, String> = StdStringValidator.isNotEmpty()

            "when a string is empty" - {
                val str = ""

                "then the validator should return an error" {
                    val failure = validator.validate(ENV, LOCATION, str)

                    failure.shouldNotBeNull()
                    failure shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Strings.IsEmpty
                    )
                }
            }

            "when a string is blank" - {
                val str = " "

                "then the validator should return the null value" {
                    val errors = validator.validate(ENV, LOCATION, str)
                    errors.shouldBeNull()
                }
            }

            "when a string is not blank" - {
                val str = "a"

                "then the validator should return the null value" {
                    val errors = validator.validate(ENV, LOCATION, str)
                    errors.shouldBeNull()
                }
            }
        }
    }

    internal class EB : IsNotEmptyStringValidator.ErrorBuilder {
        override fun isNotEmptyStringError(): ReaderResult.Error =
            JsonErrors.Validation.Strings.IsEmpty
    }
}
