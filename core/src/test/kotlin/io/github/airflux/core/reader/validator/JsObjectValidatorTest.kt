package io.github.airflux.core.reader.validator

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.dsl.reader.`object`.ObjectValuesMap
import io.github.airflux.dsl.reader.`object`.ObjectValuesMapInstance
import io.github.airflux.dsl.reader.`object`.property.JsObjectProperties
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class JsObjectValidatorTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext()
        private val LOCATION = JsLocation.empty
        private val VALUE = JsObject()
        private val VALUES: ObjectValuesMap = ObjectValuesMapInstance()
        private val PROPERTIES = JsObjectProperties(emptyList())
    }

    init {

        "The JsObjectValidator#Before type" - {

            "composition OR operator" - {

                "when the left validator returns success" - {
                    val leftValidator = JsObjectValidator.Before { _, _, _, _ -> null }

                    "then the right validator does not execute" {
                        val rightValidator = JsObjectValidator.Before { _, location, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        val composeValidator = leftValidator or rightValidator
                        val errors = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUE)

                        errors.shouldBeNull()
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = JsObjectValidator.Before { _, location, _, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "when the right validator returns success" - {
                        val rightValidator = JsObjectValidator.Before { _, _, _, _ -> null }

                        "then failure of the left validator is returned" {
                            val composeValidator = leftValidator or rightValidator
                            val errors = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUE)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = JsObjectValidator.Before { _, location, _, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                            )
                        }

                        "then both errors are returned" {
                            val composeValidator = leftValidator or rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUE)

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
                    val leftValidator = JsObjectValidator.Before { _, _, _, _ -> null }

                    "when the right validator returns success" - {
                        val rightValidator = JsObjectValidator.Before { _, _, _, _ -> null }

                        "then success is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUE)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = JsObjectValidator.Before { _, location, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        "then failure of the right validator is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUE)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = JsObjectValidator.Before { _, location, _, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "then the right validator does not execute" - {
                        val rightValidator = JsObjectValidator.Before { _, location, _, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                            )
                        }

                        val composeValidator = leftValidator and rightValidator
                        val failure = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUE)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                    }
                }
            }
        }

        "The JsObjectValidator#After type" - {

            "composition OR operator" - {

                "when the left validator returns success" - {
                    val leftValidator = JsObjectValidator.After { _, _, _, _, _ -> null }

                    "then the right validator does not execute" {
                        val rightValidator = JsObjectValidator.After { _, location, _, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        val composeValidator = leftValidator or rightValidator
                        val errors = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUES, VALUE)

                        errors.shouldBeNull()
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = JsObjectValidator.After { _, location, _, _, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "when the right validator returns success" - {
                        val rightValidator = JsObjectValidator.After { _, _, _, _, _ -> null }

                        "then failure of the left validator is returned" {
                            val composeValidator = leftValidator or rightValidator
                            val errors = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUES, VALUE)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = JsObjectValidator.After { _, location, _, _, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                            )
                        }

                        "then both errors are returned" {
                            val composeValidator = leftValidator or rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUES, VALUE)

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
                    val leftValidator = JsObjectValidator.After { _, _, _, _, _ -> null }

                    "when the right validator returns success" - {
                        val rightValidator = JsObjectValidator.After { _, _, _, _, _ -> null }

                        "then success is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUES, VALUE)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = JsObjectValidator.After { _, location, _, _, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        "then failure of the right validator is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUES, VALUE)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = JsObjectValidator.After { _, location, _, _, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "then the right validator does not execute" - {
                        val rightValidator = JsObjectValidator.After { _, location, _, _, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                            )
                        }

                        val composeValidator = leftValidator and rightValidator
                        val failure = composeValidator.validation(CONTEXT, LOCATION, PROPERTIES, VALUES, VALUE)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                    }
                }
            }
        }
    }
}
