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

package io.github.airflux.dsl.reader.`object`.builder.validator

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.dsl.reader.`object`.builder.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.builder.ObjectValuesMapInstance
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.validator.JsObjectValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class JsObjectValidatorBuilderOpsTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext()
        private val LOCATION = JsLocation.empty
        private val INPUT = JsObject()
        private val VALUES_MAP: ObjectValuesMap = ObjectValuesMapInstance()
        private val PROPERTIES = JsObjectProperties(emptyList())
    }

    init {

        "The JsObjectValidatorBuilder#Before type" - {

            "composition OR operator" - {

                "when the left validator is null" - {
                    val leftValidatorBuilder: JsObjectValidatorBuilder.Before? = null

                    "when the right validator returns success" - {
                        val rightValidatorBuilderBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, _, _, _ -> null }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val errors = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, location, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns success" - {
                    val leftValidatorBuilder = JsObjectValidatorBuilder.Before {
                        JsObjectValidator.Before { _, _, _, _ -> null }
                    }

                    "then the right validator does not execute" {
                        val rightValidatorBuilderBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, location, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilderBuilder
                        val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                        val errors = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)

                        errors.shouldBeNull()
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidatorBuilder = JsObjectValidatorBuilder.Before {
                        JsObjectValidator.Before { _, location, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }
                    }

                    "when the right validator returns success" - {
                        val rightValidatorBuilderBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, _, _, _ -> null }
                        }

                        "then failure of the left validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val errors = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, location, _, _ ->
                                JsResult.Failure(
                                    location,
                                    JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.BOOLEAN
                                    )
                                )
                            }
                        }

                        "then both errors are returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)

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

                "when the left validator is null" - {
                    val leftValidatorBuilder: JsObjectValidatorBuilder.Before? = null

                    "when the right validator returns success" - {
                        val rightValidatorBuilderBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, _, _, _ -> null }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilderBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, location, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns success" - {
                    val leftValidatorBuilder = JsObjectValidatorBuilder.Before {
                        JsObjectValidator.Before { _, _, _, _ -> null }
                    }

                    "when the right validator returns success" - {
                        val rightValidatorBuilderBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, _, _, _ -> null }
                        }

                        "then success is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilderBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, location, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then failure of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidatorBuilder = JsObjectValidatorBuilder.Before {
                        JsObjectValidator.Before { _, location, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }
                    }

                    "then the right validator does not execute" - {
                        val rightValidatorBuilderBuilder = JsObjectValidatorBuilder.Before {
                            JsObjectValidator.Before { _, location, _, _ ->
                                JsResult.Failure(
                                    location,
                                    JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.BOOLEAN
                                    )
                                )
                            }
                        }

                        val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilderBuilder
                        val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                        val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, INPUT)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                    }
                }
            }
        }

        "The JsObjectValidatorBuilder#After type" - {

            "composition OR operator" - {

                "when the left validator is null" - {
                    val leftValidatorBuilder: JsObjectValidatorBuilder.After? = null

                    "when the right validator returns success" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, _, _, _, _ -> null }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val errors = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, location, _, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns success" - {
                    val leftValidatorBuilder = JsObjectValidatorBuilder.After {
                        JsObjectValidator.After { _, _, _, _, _ -> null }
                    }

                    "then the right validator does not execute" {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, location, _, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                        val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                        val errors = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)

                        errors.shouldBeNull()
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidatorBuilder = JsObjectValidatorBuilder.After {
                        JsObjectValidator.After { _, location, _, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }
                    }

                    "when the right validator returns success" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, _, _, _, _ -> null }
                        }

                        "then failure of the left validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val errors = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, location, _, _, _ ->
                                JsResult.Failure(
                                    location,
                                    JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.BOOLEAN
                                    )
                                )
                            }
                        }

                        "then both errors are returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)

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

                "when the left validator is null" - {
                    val leftValidatorBuilder: JsObjectValidatorBuilder.After? = null

                    "when the right validator returns success" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, _, _, _, _ -> null }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, location, _, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns success" - {
                    val leftValidatorBuilder = JsObjectValidatorBuilder.After {
                        JsObjectValidator.After { _, _, _, _, _ -> null }
                    }

                    "when the right validator returns success" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, _, _, _, _ -> null }
                        }

                        "then success is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, location, _, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then failure of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                            val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidatorBuilder = JsObjectValidatorBuilder.After {
                        JsObjectValidator.After { _, location, _, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }
                    }

                    "then the right validator does not execute" - {
                        val rightValidatorBuilder = JsObjectValidatorBuilder.After {
                            JsObjectValidator.After { _, location, _, _, _ ->
                                JsResult.Failure(
                                    location,
                                    JsonErrors.InvalidType(
                                        expected = JsValue.Type.STRING,
                                        actual = JsValue.Type.BOOLEAN
                                    )
                                )
                            }
                        }

                        val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilder
                        val composeValidator = composeValidatorBuilder.build(PROPERTIES)
                        val failure = composeValidator.validate(CONTEXT, LOCATION, PROPERTIES, VALUES_MAP, INPUT)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                    }
                }
            }
        }
    }
}
