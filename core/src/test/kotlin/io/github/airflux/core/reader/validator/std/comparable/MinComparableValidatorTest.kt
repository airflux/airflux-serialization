package io.github.airflux.core.reader.validator.std.comparable

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.JsValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MinComparableValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
        private const val MIN_VALUE: Int = 2
    }

    init {

        "The string validator Min" - {
            val validator: JsValidator<Int> = ComparableValidator.min(MIN_VALUE)

            "when the reader context does not contain the error builder" - {
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, MIN_VALUE - 1)
                    }
                    exception.message shouldBe "Key '${MinComparableValidator.ErrorBuilder.Key.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    MinComparableValidator.ErrorBuilder(JsonErrors.Validation.Numbers::Min)
                )

                "when a value is less than the min allowed" - {
                    val value = MIN_VALUE - 1

                    "then the validator should return an error" {
                        val errors = validator.validation(context, LOCATION, value)

                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            JsonErrors.Validation.Numbers.Min(expected = MIN_VALUE, actual = value)
                        )
                    }
                }

                "when a value is equal to the min allowed" - {
                    val value = MIN_VALUE

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, value)
                        errors.shouldBeNull()
                    }
                }

                "when a value is more than the min allowed" - {
                    val value = MIN_VALUE + 1

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, value)
                        errors.shouldBeNull()
                    }
                }
            }
        }
    }
}
