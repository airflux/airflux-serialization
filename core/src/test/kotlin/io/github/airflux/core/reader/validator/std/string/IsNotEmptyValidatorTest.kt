package io.github.airflux.core.reader.validator.std.string

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

internal class IsNotEmptyValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
    }

    init {

        "The string validator IsNotEmpty" - {
            val validator: JsValidator<String> = StringValidator.isNotEmpty

            "when the reader context does not contain the error builder" - {
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, "")
                    }
                    exception.message shouldBe "Key '${IsNotEmptyStringValidator.ErrorBuilder.Key.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    IsNotEmptyStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty }
                )

                "when a string is empty" - {
                    val str = ""

                    "then the validator should return an error" {
                        val errors = validator.validation(context, LOCATION, str)

                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when a string is blank" - {
                    val str = " "

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, str)
                        errors.shouldBeNull()
                    }
                }

                "when a string is not blank" - {
                    val str = "a"

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, str)
                        errors.shouldBeNull()
                    }
                }
            }
        }
    }
}
