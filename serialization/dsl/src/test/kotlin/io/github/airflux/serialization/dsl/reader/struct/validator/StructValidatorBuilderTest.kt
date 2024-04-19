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

package io.github.airflux.serialization.dsl.reader.struct.validator

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.validation.JsValidatorResult
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validation.StructValidator
import io.github.airflux.serialization.dsl.reader.struct.validation.and
import io.github.airflux.serialization.dsl.reader.struct.validation.or
import io.github.airflux.serialization.test.kotest.shouldBeInvalid
import io.github.airflux.serialization.test.kotest.shouldBeValid
import io.kotest.core.spec.style.FreeSpec

internal class StructValidatorBuilderTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(Unit, Unit)
        private val LOCATION: JsLocation = JsLocation
        private val PROPERTIES: StructProperties<Unit, Unit> = emptyList()
        private val SOURCE: JsStruct = JsStruct()
    }

    init {

        "The JsStructValidator#Builder type" - {

            "testing the or composite operator" - {

                "if the left validator returns success then the right validator doesn't execute" {
                    val leftValidator = StructValidator.Builder { _ -> createValidator(valid()) }

                    val rightValidator = StructValidator.Builder { _ ->
                        createValidator { location -> invalid(location, ValidationErrors.PathMissing) }
                    }

                    val builder = leftValidator or rightValidator
                    val composeValidator = builder.build(PROPERTIES)
                    val result = composeValidator.validate(ENV, LOCATION, PROPERTIES, SOURCE)

                    result.shouldBeValid()
                }

                "if the left validator returns an error" - {
                    val leftValidator = StructValidator.Builder { _ ->
                        createValidator { location -> invalid(location, ValidationErrors.PathMissing) }
                    }

                    "and the right validator returns success then returning the first error" {
                        val rightValidator = StructValidator.Builder { _ -> createValidator(valid()) }

                        val builder = leftValidator or rightValidator
                        val composeValidator = builder.build(PROPERTIES)
                        val result = composeValidator.validate(ENV, LOCATION, PROPERTIES, SOURCE)

                        result.shouldBeValid()
                    }

                    "and the right validator returns an error then returning both errors" {
                        val rightValidator = StructValidator.Builder { _ ->
                            createValidator { location -> invalid(location, ValidationErrors.InvalidType) }
                        }

                        val builder = leftValidator or rightValidator
                        val composeValidator = builder.build(PROPERTIES)
                        val result = composeValidator.validate(ENV, LOCATION, PROPERTIES, SOURCE)

                        result shouldBeInvalid
                            JsReaderResult.Failure(LOCATION, ValidationErrors.PathMissing) +
                            JsReaderResult.Failure(LOCATION, ValidationErrors.InvalidType)
                    }
                }
            }

            "testing the and composite operator" - {

                "if the left validator returns an error then the right validator doesn't execute" {
                    val leftValidator = StructValidator.Builder { _ ->
                        createValidator { location -> invalid(location, ValidationErrors.PathMissing) }
                    }

                    val rightValidator = StructValidator.Builder { _ ->
                        createValidator { location -> invalid(location, ValidationErrors.InvalidType) }
                    }

                    val builder = leftValidator and rightValidator
                    val composeValidator = builder.build(PROPERTIES)
                    val result = composeValidator.validate(ENV, LOCATION, PROPERTIES, SOURCE)

                    result shouldBeInvalid failure(LOCATION, ValidationErrors.PathMissing)
                }

                "if the left validator returns a success" - {
                    val leftValidator = StructValidator.Builder { _ -> createValidator(valid()) }

                    "and the second validator returns success, then success is returned" {
                        val rightValidator = StructValidator.Builder { _ -> createValidator(valid()) }

                        val builder = leftValidator and rightValidator
                        val composeValidator = builder.build(PROPERTIES)
                        val result = composeValidator.validate(ENV, LOCATION, PROPERTIES, SOURCE)

                        result.shouldBeValid()
                    }

                    "and the right validator returns an error, then an error is returned" {
                        val rightValidator = StructValidator.Builder { _ ->
                            createValidator { location -> invalid(location, ValidationErrors.PathMissing) }
                        }

                        val builder = leftValidator and rightValidator
                        val composeValidator = builder.build(PROPERTIES)
                        val result = composeValidator.validate(ENV, LOCATION, PROPERTIES, SOURCE)

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

    private fun createValidator(result: (JsLocation) -> JsValidatorResult): StructValidator<Unit, Unit> =
        StructValidator { _, location, _, _ -> result(location) }

    private fun createValidator(result: JsValidatorResult): StructValidator<Unit, Unit> =
        StructValidator { _, _, _, _ -> result }
}
