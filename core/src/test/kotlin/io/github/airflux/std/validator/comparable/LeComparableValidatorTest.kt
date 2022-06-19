package io.github.airflux.std.validator.comparable

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.contextKeyName
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.JsValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class LeComparableValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
        private const val VALUE: Int = 2
    }

    init {

        "The string validator Ge" - {
            val validator: JsValidator<Int> = ComparableValidator.le(VALUE)

            "when the reader context does not contain the error builder" - {
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, VALUE + 1)
                    }
                    exception.message shouldBe "Key '${LeComparableValidator.ErrorBuilder.contextKeyName()}' is missing in the context of reading."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    LeComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Le)
                )

                "when a value is less than the allowed value" - {
                    val value = VALUE - 1

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, value)
                        errors.shouldBeNull()
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

                    "then the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, value)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Numbers.Le(expected = VALUE, actual = value)
                        )
                    }
                }
            }
        }
    }
}
