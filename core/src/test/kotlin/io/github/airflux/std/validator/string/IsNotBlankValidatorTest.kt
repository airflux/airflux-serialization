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

internal class IsNotBlankValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
    }

    init {

        "The string validator IsNotBlank" - {
            val validator: JsValidator<String> = StringValidator.isNotBlank

            "when the reader context does not contain the error builder" - {
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, "")
                    }
                    exception.message shouldBe "Key '${IsNotBlankStringValidator.ErrorBuilder.contextKeyName()}' is missing in the context of reading."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    IsNotBlankStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsBlank }
                )

                "when a string is empty" - {
                    val str = ""

                    "then the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, str)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsBlank
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
                            error = JsonErrors.Validation.Strings.IsBlank
                        )
                    }
                }

                "when a string is not blank" - {
                    val str = " a "

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, str)
                        errors.shouldBeNull()
                    }
                }
            }
        }
    }
}
