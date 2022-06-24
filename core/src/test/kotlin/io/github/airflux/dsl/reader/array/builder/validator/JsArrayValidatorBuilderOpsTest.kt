package io.github.airflux.dsl.reader.array.builder.validator

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.github.airflux.dsl.reader.validator.JsArrayValidator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class JsArrayValidatorBuilderOpsTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext()
        private val LOCATION = JsLocation.empty
        private val INPUT = JsArray<JsString>()
        private val VALUES: List<String> = emptyList()
    }

    init {

        "The JsArrayValidatorBuilder#Before type" - {

            "composition OR operator" - {

                "when the left validator is null" - {
                    val leftValidatorBuilder: JsArrayValidatorBuilder.Before? = null

                    "when the right validator returns success" - {
                        val rightValidatorBuilderBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, _, _ -> null }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val errors = composeValidator.validation(CONTEXT, LOCATION, INPUT)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, location, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns success" - {
                    val leftValidatorBuilder = JsArrayValidatorBuilder.Before {
                        JsArrayValidator.Before { _, _, _ -> null }
                    }

                    "then the right validator does not execute" {
                        val rightValidatorBuilderBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, location, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilderBuilder
                        val composeValidator = composeValidatorBuilder.build()
                        val errors = composeValidator.validation(CONTEXT, LOCATION, INPUT)

                        errors.shouldBeNull()
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidatorBuilder = JsArrayValidatorBuilder.Before {
                        JsArrayValidator.Before { _, location, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }
                    }

                    "when the right validator returns success" - {
                        val rightValidatorBuilderBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, _, _ -> null }
                        }

                        "then failure of the left validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val errors = composeValidator.validation(CONTEXT, LOCATION, INPUT)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, location, _ ->
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
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT)

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
                    val leftValidatorBuilder: JsArrayValidatorBuilder.Before? = null

                    "when the right validator returns success" - {
                        val rightValidatorBuilderBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, _, _ -> null }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilderBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, location, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns success" - {
                    val leftValidatorBuilder = JsArrayValidatorBuilder.Before {
                        JsArrayValidator.Before { _, _, _ -> null }
                    }

                    "when the right validator returns success" - {
                        val rightValidatorBuilderBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, _, _ -> null }
                        }

                        "then success is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilderBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, location, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then failure of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilderBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidatorBuilder = JsArrayValidatorBuilder.Before {
                        JsArrayValidator.Before { _, location, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }
                    }

                    "then the right validator does not execute" - {
                        val rightValidatorBuilderBuilder = JsArrayValidatorBuilder.Before {
                            JsArrayValidator.Before { _, location, _ ->
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
                        val composeValidator = composeValidatorBuilder.build()
                        val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                    }
                }
            }
        }

        "The JsArrayValidatorBuilder#After type" - {

            "composition OR operator" - {

                "when the left validator is null" - {
                    val leftValidatorBuilder: JsArrayValidatorBuilder.After<String>? = null

                    "when the right validator returns success" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, _, _, _ -> null }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val errors = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, location, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns success" - {
                    val leftValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                        JsArrayValidator.After { _, _, _, _ -> null }
                    }

                    "then the right validator does not execute" {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, location, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                        val composeValidator = composeValidatorBuilder.build()
                        val errors = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)

                        errors.shouldBeNull()
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                        JsArrayValidator.After { _, location, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }
                    }

                    "when the right validator returns success" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, _, _, _ -> null }
                        }

                        "then failure of the left validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder or rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val errors = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, location, _, _ ->
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
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)

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
                    val leftValidatorBuilder: JsArrayValidatorBuilder.After<String>? = null

                    "when the right validator returns success" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, _, _, _ -> null }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, location, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then the result of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns success" - {
                    val leftValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                        JsArrayValidator.After { _, _, _, _ -> null }
                    }

                    "when the right validator returns success" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, _, _, _ -> null }
                        }

                        "then success is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, location, _, _ ->
                                JsResult.Failure(location, JsonErrors.PathMissing)
                            }
                        }

                        "then failure of the right validator is returned" {
                            val composeValidatorBuilder = leftValidatorBuilder and rightValidatorBuilder
                            val composeValidator = composeValidatorBuilder.build()
                            val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                        JsArrayValidator.After { _, location, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }
                    }

                    "then the right validator does not execute" - {
                        val rightValidatorBuilder = JsArrayValidatorBuilder.After<String> {
                            JsArrayValidator.After { _, location, _, _ ->
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
                        val composeValidator = composeValidatorBuilder.build()
                        val failure = composeValidator.validation(CONTEXT, LOCATION, INPUT, VALUES)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                    }
                }
            }
        }
    }
}
