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

package io.github.airflux.serialization.std.validator.condition

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.validator.Validator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class ConditionValidatorsTest : FreeSpec() {

    companion object {
        private val CONTEXT = ReaderContext()
        private val LOCATION = Location.empty
        private val isNotEmpty: Validator<String> =
            Validator { _, location, value ->
                if (value.isNotEmpty())
                    null
                else
                    JsResult.Failure(location = location, error = JsonErrors.Validation.Strings.IsEmpty)
            }
    }

    init {

        "JsPropertyValidator<T>#applyIfNotNull()" - {
            val validator = isNotEmpty.applyIfNotNull()

            "should return the result of applying the validator to the value if it is not the null value" {
                val failure = validator.validate(CONTEXT, LOCATION, "")

                failure.shouldNotBeNull()
                failure shouldBe JsResult.Failure(
                    location = LOCATION,
                    error = JsonErrors.Validation.Strings.IsEmpty
                )
            }

            "should return the null value if the value is the null value" {
                val errors = validator.validate(CONTEXT, LOCATION, null)

                errors.shouldBeNull()
            }
        }

        "JsPropertyValidator<T>#applyIf(_)" - {

            "should return the result of applying the validator to the value if the predicate returns true" {
                val validator = isNotEmpty.applyIf { _, _, _ -> true }

                val failure = validator.validate(CONTEXT, LOCATION, "")

                failure.shouldNotBeNull()
                failure shouldBe JsResult.Failure(
                    location = LOCATION,
                    error = JsonErrors.Validation.Strings.IsEmpty
                )
            }

            "should return the null value if the predicate returns false" {
                val validator = isNotEmpty.applyIf { _, _, _ -> false }

                val errors = validator.validate(CONTEXT, LOCATION, "")

                errors.shouldBeNull()
            }
        }
    }
}
