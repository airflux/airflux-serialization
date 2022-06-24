package io.github.airflux.std.validator.comparable

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.context.error.errorBuilderName
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.JsValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class GeComparableValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
        private const val VALUE: Int = 2
    }

    init {

        "The string validator Ge" - {
            val validator: JsValidator<Int> = ComparableValidator.ge(VALUE)

            "when the reader context does not contain the error builder" - {
                val context = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, VALUE - 1)
                    }
                    exception.message shouldBe "The error builder '${GeComparableValidator.ErrorBuilder.errorBuilderName()}' is missing in the context."
                }
            }

            "when the reader context contains the error builder" - {
                val context = JsReaderContext(
                    GeComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Ge)
                )

                "when a value is less than the allowed value" - {
                    val value = VALUE - 1

                    "then the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, value)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Numbers.Ge(expected = VALUE, actual = value)
                        )
                    }
                }

                "when a value is equal to the allowed value" - {
                    val value = VALUE

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, value)
                        errors.shouldBeNull()
                    }
                }

                "when a value is greater than the allowed value" - {
                    val value = VALUE + 1

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, value)
                        errors.shouldBeNull()
                    }
                }
            }
        }
    }
}
