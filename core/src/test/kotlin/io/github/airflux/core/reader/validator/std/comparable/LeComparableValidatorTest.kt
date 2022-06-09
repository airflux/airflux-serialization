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
                    exception.message shouldBe "Key '${LeComparableValidator.ErrorBuilder.Key.name}' is missing in the JsReaderContext."
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
                        val errors = validator.validation(context, LOCATION, value)

                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            JsonErrors.Validation.Numbers.Le(expected = VALUE, actual = value)
                        )
                    }
                }
            }
        }
    }
}