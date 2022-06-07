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

internal class IsAValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
        private val PATTERN: Regex = "\\d+".toRegex()
        private val IS_DIGITAL: (String) -> Boolean = { value: String -> PATTERN.matches(value) }
    }

    init {

        "The string validator MinLength" - {
            val validator: JsValidator<String> = StringValidator.isA(IS_DIGITAL)

            "when the reader context does not contain the error builder" - {
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, "a")
                    }
                    exception.message shouldBe "Key '${IsAStringValidator.ErrorBuilder.Key.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    IsAStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::IsA)
                )

                "when a string is empty" - {
                    val str = ""

                    "then the validator should return an error" {
                        val errors = validator.validation(context, LOCATION, str)

                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            JsonErrors.Validation.Strings.IsA(value = str)
                        )
                    }
                }

                "when a string is blank" - {
                    val str = " "

                    "then the validator should return an error" {
                        val errors = validator.validation(context, LOCATION, str)

                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            JsonErrors.Validation.Strings.IsA(value = str)
                        )
                    }
                }

                "when the string is not a digital" - {
                    val str = "a"

                    "then the validator should return an error" {
                        val errors = validator.validation(context, LOCATION, str)

                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            JsonErrors.Validation.Strings.IsA(value = str)
                        )
                    }
                }

                "when the string is a digital" - {
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
