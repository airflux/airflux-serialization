package io.github.airflux.dsl.reader.array.validator.std

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsString
import io.github.airflux.dsl.common.JsonErrors
import io.github.airflux.dsl.reader.array.validator.std.ArrayValidator.minItems
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MinItemsValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation.empty
        private const val MIN_ITEMS = 2
    }

    init {

        "The array validator MinItems" - {
            val validator = minItems(MIN_ITEMS).build()

            "when the reader context does not contain the error builder" - {
                val context = JsReaderContext()

                "when the test condition is false" {
                    val input: JsArray<JsString> = JsArray(JsString("A"))

                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, input)
                    }
                    exception.message shouldBe "Key '${MinItemsArrayValidator.ErrorBuilder.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    MinItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MinItems)
                )

                "when a collection is empty" - {
                    val input: JsArray<JsString> = JsArray()

                    "the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, input)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Arrays.MinItems(expected = MIN_ITEMS, actual = input.size)
                        )
                    }
                }

                "when the collection contains a number of elements less than the minimum" - {
                    val input: JsArray<JsString> = JsArray(JsString("A"))

                    "then the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, input)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Arrays.MinItems(expected = MIN_ITEMS, actual = input.size)
                        )
                    }
                }

                "when the collection contains a number of elements equal to the minimum" - {
                    val input: JsArray<JsString> = JsArray(JsString("A"), JsString("B"))

                    "then the validator should do not return any errors" {
                        val failure = validator.validation(context, LOCATION, input)
                        failure.shouldBeNull()
                    }
                }

                "when the collection contains a number of elements more than the minimum" - {
                    val input: JsArray<JsString> = JsArray(JsString("A"), JsString("B"), JsString("C"))

                    "then the validator should do not return any errors" {
                        val failure = validator.validation(context, LOCATION, input)
                        failure.shouldBeNull()
                    }
                }
            }
        }
    }
}
