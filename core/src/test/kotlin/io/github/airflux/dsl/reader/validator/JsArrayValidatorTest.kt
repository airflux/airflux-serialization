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

package io.github.airflux.dsl.reader.validator

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class JsArrayValidatorTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext()
        private val LOCATION = JsLocation.empty
        private val VALUE = JsArray<JsString>()
    }

    init {

        "The JsArrayValidator#Before type" - {

            "composition OR operator" - {

                "when the left validator returns success" - {
                    val leftValidator = JsArrayValidator.Before { _, _, _ -> null }

                    "then the right validator does not execute" {
                        val rightValidator = JsArrayValidator.Before { _, location, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        val composeValidator = leftValidator or rightValidator
                        val errors = composeValidator.validation(CONTEXT, LOCATION, VALUE)

                        errors.shouldBeNull()
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = JsArrayValidator.Before { _, location, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "when the right validator returns success" - {
                        val rightValidator = JsArrayValidator.Before { _, _, _ -> null }

                        "then failure of the left validator is returned" {
                            val composeValidator = leftValidator or rightValidator
                            val errors = composeValidator.validation(CONTEXT, LOCATION, VALUE)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = JsArrayValidator.Before { _, location, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                            )
                        }

                        "then both errors are returned" {
                            val composeValidator = leftValidator or rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, VALUE)

                            failure.shouldNotBeNull()
                            failure shouldBe listOf(
                                JsResult.Failure(LOCATION, JsonErrors.PathMissing),
                                JsResult.Failure(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.BOOLEAN
                                    )
                                )
                            ).merge()
                        }
                    }
                }
            }

            "composition AND operator" - {

                "when the left validator returns success" - {
                    val leftValidator = JsArrayValidator.Before { _, _, _ -> null }

                    "when the right validator returns success" - {
                        val rightValidator = JsArrayValidator.Before { _, _, _ -> null }

                        "then success is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, VALUE)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = JsArrayValidator.Before { _, location, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        "then failure of the right validator is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, VALUE)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = JsArrayValidator.Before { _, location, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "then the right validator does not execute" - {
                        val rightValidator = JsArrayValidator.Before { _, location, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                            )
                        }

                        val composeValidator = leftValidator and rightValidator
                        val failure = composeValidator.validation(CONTEXT, LOCATION, VALUE)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                    }
                }
            }
        }

        "The JsArrayValidator#After type" - {

            "composition OR operator" - {

                "when the left validator returns success" - {
                    val leftValidator = JsArrayValidator.After<Unit> { _, _, _, _ -> null }

                    "then the right validator does not execute" {
                        val rightValidator = JsArrayValidator.After<Unit> { _, location, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        val composeValidator = leftValidator or rightValidator
                        val errors = composeValidator.validation(CONTEXT, LOCATION, VALUE, listOf(Unit))

                        errors.shouldBeNull()
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = JsArrayValidator.After<Unit> { _, location, _, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "when the right validator returns success" - {
                        val rightValidator = JsArrayValidator.After<Unit> { _, _, _, _ -> null }

                        "then failure of the left validator is returned" {
                            val composeValidator = leftValidator or rightValidator
                            val errors = composeValidator.validation(CONTEXT, LOCATION, VALUE, listOf(Unit))

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = JsArrayValidator.After<Unit> { _, location, _, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                            )
                        }

                        "then both errors are returned" {
                            val composeValidator = leftValidator or rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, VALUE, listOf(Unit))

                            failure.shouldNotBeNull()
                            failure shouldBe listOf(
                                JsResult.Failure(LOCATION, JsonErrors.PathMissing),
                                JsResult.Failure(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.BOOLEAN
                                    )
                                )
                            ).merge()
                        }
                    }
                }
            }

            "composition AND operator" - {

                "when the left validator returns success" - {
                    val leftValidator = JsArrayValidator.After<Unit> { _, _, _, _ -> null }

                    "when the right validator returns success" - {
                        val rightValidator = JsArrayValidator.After<Unit> { _, _, _, _ -> null }

                        "then success is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, VALUE, listOf(Unit))
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = JsArrayValidator.After<Unit> { _, location, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        "then failure of the right validator is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, VALUE, listOf(Unit))
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = JsArrayValidator.After<Unit> { _, location, _, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "then the right validator does not execute" - {
                        val rightValidator = JsArrayValidator.After<Unit> { _, location, _, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                            )
                        }

                        val composeValidator = leftValidator and rightValidator
                        val failure = composeValidator.validation(CONTEXT, LOCATION, VALUE, listOf(Unit))

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                    }
                }
            }
        }
    }
}
