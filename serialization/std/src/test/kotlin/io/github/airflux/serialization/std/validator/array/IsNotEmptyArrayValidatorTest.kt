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

package io.github.airflux.serialization.std.validator.array

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.array.validation.JsArrayValidator
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.kotest.assertions.shouldBeInvalid
import io.github.airflux.serialization.kotest.assertions.shouldBeValid
import io.github.airflux.serialization.std.common.JsonErrors
import io.kotest.core.spec.style.FreeSpec

internal class IsNotEmptyArrayValidatorTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(config = JsReaderEnv.Config(EB(), Unit))
        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The array validator IsNotEmpty" - {
            val validator: JsArrayValidator<EB, Unit> = StdArrayValidator.isNotEmpty()

            "when an array is empty" - {
                val source = JsArray()

                "then the validator should return an error" {
                    val result = validator.validate(ENV, LOCATION, source)

                    result shouldBeInvalid JsValidatorResult.Invalid(
                        failure = JsReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Arrays.IsEmpty
                        )
                    )
                }
            }

            "when an array is not empty" - {
                val source = JsArray(JsString("A"), JsString("B"))

                "then the validator should do not return any errors" {
                    val result = validator.validate(ENV, LOCATION, source)
                    result.shouldBeValid()
                }
            }
        }
    }

    internal class EB : IsNotEmptyArrayValidator.ErrorBuilder {
        override fun isNotEmptyArrayError(): JsReaderResult.Error = JsonErrors.Validation.Arrays.IsEmpty
    }
}
