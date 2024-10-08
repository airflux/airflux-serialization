/*
 * Copyright 2021-2024 Maxim Sambulat.
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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.kotest.assertions.shouldBeInvalid
import io.github.airflux.serialization.kotest.assertions.shouldBeValid
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.test.dummy.DummyValidator
import io.kotest.core.spec.style.FreeSpec

internal class ConditionValidatorsTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(config = JsReaderEnv.Config(errorBuilders = Unit, options = Unit))
        private val LOCATION: JsLocation = JsLocation
        private val IsNotEmptyStringValidator: JsValidator<Unit, Unit, String> =
            DummyValidator.isNotEmptyString { JsonErrors.Validation.Strings.IsEmpty }
    }

    init {

        "JsPropertyValidator<T>#applyIfNotNull()" - {
            val validator = IsNotEmptyStringValidator.applyIfNotNull()

            "should return the result of applying the validator to the value if it is not the null value" {
                val result = validator.validate(ENV, LOCATION, "")

                result shouldBeInvalid JsValidatorResult.Invalid(
                    failure = JsReaderResult.Failure(location = LOCATION, error = JsonErrors.Validation.Strings.IsEmpty)
                )
            }

            "should return the null value if the value is the null value" {
                val result = validator.validate(ENV, LOCATION, null)
                result.shouldBeValid()
            }
        }

        "JsPropertyValidator<T>#applyIf(_)" - {

            "should return the result of applying the validator to the value if the predicate returns true" {
                val validator = IsNotEmptyStringValidator.applyIf { _, _, _ -> true }

                val result = validator.validate(ENV, LOCATION, "")

                result shouldBeInvalid JsValidatorResult.Invalid(
                    failure = JsReaderResult.Failure(location = LOCATION, error = JsonErrors.Validation.Strings.IsEmpty)
                )
            }

            "should return the null value if the predicate returns false" {
                val validator = IsNotEmptyStringValidator.applyIf { _, _, _ -> false }

                val result = validator.validate(ENV, LOCATION, "")
                result.shouldBeValid()
            }
        }
    }
}
