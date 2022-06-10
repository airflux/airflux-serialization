package io.github.airflux.core.reader.validator.std.comparable

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
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
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, VALUE - 1)
                    }
                    exception.message shouldBe "Key '${GeComparableValidator.ErrorBuilder.Key.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
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
