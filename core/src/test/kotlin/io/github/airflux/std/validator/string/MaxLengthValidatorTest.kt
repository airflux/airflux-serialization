package io.github.airflux.std.validator.string

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.contextKeyName
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.JsValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MaxLengthValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
        private const val MAX_VALUE: Int = 2
    }

    init {

        "The string validator MaxLength" - {
            val validator: JsValidator<String> = StringValidator.maxLength(MAX_VALUE)

            "when the reader context does not contain the error builder" - {
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, "abc")
                    }
                    exception.message shouldBe "Key '${MaxLengthStringValidator.ErrorBuilder.contextKeyName()}' is missing in the context of reading."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    MaxLengthStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::MaxLength)
                )

                "when a string is empty" - {
                    val str = ""

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, str)
                        errors.shouldBeNull()
                    }
                }

                "when a string is blank" - {

                    "when the length of the string is less the max allowed length" - {
                        val str = " "

                        "then the validator should return the null value" {
                            val errors = validator.validation(context, LOCATION, str)
                            errors.shouldBeNull()
                        }
                    }

                    "when the length of the string is equal to the max allowed length" - {
                        val str = "  "

                        "then the validator should return the null value" {
                            val errors = validator.validation(context, LOCATION, str)
                            errors.shouldBeNull()
                        }
                    }

                    "when the length of the string is more the max allowed length" - {
                        val str = "   "

                        "then the validator should return an error" {
                            val failure = validator.validation(context, LOCATION, str)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.MaxLength(
                                    expected = MAX_VALUE,
                                    actual = str.length
                                )
                            )
                        }
                    }
                }

                "when a string is not blank" - {

                    "when the length of the string is less the max allowed length" - {
                        val str = "a"

                        "then the validator should return the null value" {
                            val errors = validator.validation(context, LOCATION, str)
                            errors.shouldBeNull()
                        }
                    }

                    "when the length of the string is equal to the max allowed length" - {
                        val str = "ab"

                        "then the validator should return the null value" {
                            val errors = validator.validation(context, LOCATION, str)
                            errors.shouldBeNull()
                        }
                    }

                    "when the length of the string is more the max allowed length" - {
                        val str = "abc"

                        "then the validator should return an error" {
                            val failure = validator.validation(context, LOCATION, str)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.MaxLength(
                                    expected = MAX_VALUE,
                                    actual = str.length
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
