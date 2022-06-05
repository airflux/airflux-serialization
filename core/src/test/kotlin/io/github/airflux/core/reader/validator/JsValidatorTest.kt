package io.github.airflux.core.reader.validator

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.result.JsLocation
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

internal class JsValidatorTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext()
        private val location = JsLocation.empty
    }

    init {

        "A JsValidator type" - {

            "testing the or composite operator" - {

                "if the left validator returns success then the right validator doesn't execute" {
                    val leftValidator = JsValidator<Unit> { _, _, _ -> null }

                    val rightValidator = JsValidator<Unit> { _, _, _ ->
                        JsErrors.of(ValidationErrors.PathMissing)
                    }

                    val composeValidator = leftValidator or rightValidator
                    val errors = composeValidator.validation(context, location, Unit)

                    errors.shouldBeNull()
                }

                "if the left validator returns an error" - {
                    val leftValidator = JsValidator<Unit> { _, _, _ ->
                        JsErrors.of(ValidationErrors.PathMissing)
                    }

                    "and the right validator returns success then returning the first error" {
                        val rightValidator = JsValidator<Unit> { _, _, _ -> null }

                        val composeValidator = leftValidator or rightValidator
                        val errors = composeValidator.validation(context, location, Unit)

                        errors.shouldBeNull()
                    }

                    "and the right validator returns an error then returning both errors" {
                        val rightValidator = JsValidator<Unit> { _, _, _ ->
                            JsErrors.of(ValidationErrors.InvalidType)
                        }

                        val composeValidator = leftValidator or rightValidator
                        val errors = composeValidator.validation(context, location, Unit)

                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            ValidationErrors.PathMissing,
                            ValidationErrors.InvalidType
                        )
                    }
                }
            }

            "testing the and composite operator" - {

                "if the left validator returns an error then the right validator doesn't execute" {
                    val leftValidator = JsValidator<Unit> { _, _, _ ->
                        JsErrors.of(ValidationErrors.PathMissing)
                    }

                    val rightValidator = JsValidator<Unit> { _, _, _ ->
                        JsErrors.of(ValidationErrors.InvalidType)
                    }

                    val composeValidator = leftValidator and rightValidator
                    val errors = composeValidator.validation(context, location, Unit)

                    errors.shouldNotBeNull()
                    errors.items shouldContainExactly listOf(ValidationErrors.PathMissing)
                }

                "if the left validator returns a success" - {
                    val leftValidator = JsValidator<Unit> { _, _, _ -> null }

                    "and the second validator returns success, then success is returned" {
                        val rightValidator = JsValidator<Unit> { _, _, _ -> null }

                        val composeValidator = leftValidator and rightValidator
                        val errors = composeValidator.validation(context, location, Unit)

                        errors.shouldBeNull()
                    }

                    "and the right validator returns an error, then an error is returned" {
                        val rightValidator = JsValidator<Unit> { _, _, _ ->
                            JsErrors.of(ValidationErrors.PathMissing)
                        }

                        val composeValidator = leftValidator and rightValidator
                        val errors = composeValidator.validation(context, location, Unit)

                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(ValidationErrors.PathMissing)
                    }
                }
            }
        }
    }

    private sealed class ValidationErrors : JsError {
        object PathMissing : ValidationErrors()
        object InvalidType : ValidationErrors()
    }
}
