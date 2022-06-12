package io.github.airflux.dsl.reader.array.validator.std

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.std.array.MaxItemsArrayValidator
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MaxItemsValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation.empty
        private const val MAX_ITEMS = 2
    }

    init {

        "The array validator MaxItems" - {
            val validator = ArrayValidator.maxItems(MAX_ITEMS).build()

            "when the reader context does not contain the error builder" - {
                val context = JsReaderContext()

                "when the test condition is false" {
                    val input: JsArray<JsString> = JsArray(JsString("A"), JsString("B"), JsString("C"))

                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, input)
                    }
                    exception.message shouldBe "Key '${MaxItemsArrayValidator.ErrorBuilder.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    MaxItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MaxItems)
                )

                "when a collection is empty" - {
                    val input: JsArray<JsString> = JsArray()

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, input)
                        errors.shouldBeNull()
                    }
                }

                "when the collection contains a number of elements less than the maximum" - {
                    val input: JsArray<JsString> = JsArray(JsString("A"))

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, input)
                        errors.shouldBeNull()
                    }
                }

                "when the collection contains a number of elements equal to the maximum" - {
                    val input: JsArray<JsString> = JsArray(JsString("A"), JsString("B"))

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, input)
                        errors.shouldBeNull()
                    }
                }

                "when the collection contains a number of elements more than the maximum" - {
                    val input: JsArray<JsString> = JsArray(JsString("A"), JsString("B"), JsString("C"))

                    "the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, input)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Arrays.MaxItems(expected = MAX_ITEMS, actual = input.size)
                        )
                    }
                }
            }
        }
    }
}
