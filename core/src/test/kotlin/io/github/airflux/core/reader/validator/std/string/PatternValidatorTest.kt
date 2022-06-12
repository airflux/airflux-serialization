package io.github.airflux.core.reader.validator.std.string

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

internal class PatternValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
        private val PATTERN: Regex = "\\d+".toRegex()
    }

    init {

        "The string validator Pattern" - {
            val validator: JsValidator<String> = StringValidator.pattern(PATTERN)

            "when the reader context does not contain the error builder" - {
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, "a")
                    }
                    exception.message shouldBe "Key '${PatternStringValidator.ErrorBuilder.contextKeyName()}' is missing in the context of reading."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    PatternStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::Pattern)
                )

                "when a string is empty" - {
                    val str = ""

                    "then the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, str)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.Pattern(value = str, regex = PATTERN)
                        )
                    }
                }

                "when a string is blank" - {
                    val str = " "

                    "then the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, str)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.Pattern(value = str, regex = PATTERN)
                        )
                    }
                }

                "when a string is not blank" - {

                    "when the string is not matching to the pattern" - {
                        val str = "a"

                        "then the validator should return an error" {
                            val failure = validator.validation(context, LOCATION, str)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.Pattern(value = str, regex = PATTERN)
                            )
                        }
                    }

                    "when the string is matching to the pattern" - {
                        val str = "123"

                        "then the validator should return the null value" {
                            val errors = validator.validation(context, LOCATION, str)
                            errors.shouldBeNull()
                        }
                    }
                }
            }
        }
    }
}
