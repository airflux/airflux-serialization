package io.github.airflux.core.reader.validator.std.array

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
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
            val validator = ArrayValidator.maxItems<String>(MAX_ITEMS)

            "when the reader context does not contain the error builder" - {
                val context = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, listOf("A", "B", "C"))
                    }
                    exception.message shouldBe "Key '${MaxItemsArrayValidator.ErrorBuilder.Key.name}' is missing in the JsReaderContext."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    MaxItemsArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::MaxItems)
                )

                "when a collection is empty" - {
                    val collection = emptyList<String>()

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, collection)
                        errors.shouldBeNull()
                    }
                }

                "when the collection contains a number of elements less than the maximum" - {
                    val collection = listOf("A")

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, collection)
                        errors.shouldBeNull()
                    }
                }

                "when the collection contains a number of elements equal to the maximum" - {
                    val collection = listOf("A", "B")

                    "then the validator should do not return any errors" {
                        val errors = validator.validation(context, LOCATION, collection)
                        errors.shouldBeNull()
                    }
                }

                "when the collection contains a number of elements more than the maximum" - {
                    val collection = listOf("A", "B", "C")

                    "the validator should return an error" {
                        val errors = validator.validation(context, LOCATION, collection)
                        errors.shouldNotBeNull()
                        errors.items shouldContainExactly listOf(
                            JsonErrors.Validation.Arrays.MaxItems(expected = MAX_ITEMS, actual = collection.size)
                        )
                    }
                }
            }
        }
    }
}
