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
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.validator.Validator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class LtComparableValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
        private const val VALUE: Int = 2
    }

    init {

        "The string validator Gt" - {
            val validator: Validator<Int> = StdComparableValidator.lt(VALUE)

            "when the reader context does not contain the error builder" - {
                val context = ReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validate(context, LOCATION, VALUE)
                    }
                    exception.message shouldBe "The error builder '${LtComparableValidator.ErrorBuilder.errorBuilderName()}' is missing in the context."
                }
            }

            "when the reader context contains the error builder" - {
                val context = ReaderContext(
                    LtComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Lt)
                )

                "when a value is less than the allowed value" - {
                    val value = VALUE - 1

                    "then the validator should return the null value" {
                        val errors = validator.validate(context, LOCATION, value)
                        errors.shouldBeNull()
                    }
                }

                "when a value is equal to the allowed value" - {
                    val value = VALUE

                    "then the validator should return an error" {
                        val failure = validator.validate(context, LOCATION, value)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Numbers.Lt(expected = VALUE, actual = value)
                        )
                    }
                }

                "when a value is greater than the allowed value" - {
                    val value = VALUE + 1

                    "then the validator should return an error" {
                        val failure = validator.validate(context, LOCATION, value)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Numbers.Lt(expected = VALUE, actual = value)
                        )
                    }
                }
            }
        }
    }
}
