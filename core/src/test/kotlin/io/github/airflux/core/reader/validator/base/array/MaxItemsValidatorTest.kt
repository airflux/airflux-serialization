package io.github.airflux.core.reader.validator.base.array

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.base.BaseArrayValidators
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

internal class MaxItemsValidatorTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext()
        private val location = JsLocation.empty
        private const val MAX_ITEMS = 2
    }

    init {

        "The maxItems validator" - {
            val validator = BaseArrayValidators.maxItems<String, List<String>>(
                expected = MAX_ITEMS,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Arrays.MaxItems(expected = expectedValue, actual = actualValue)
                }
            )

            "when a collection is empty" - {
                val collection = emptyList<String>()

                "then the validator should do not return any errors" {
                    val errors = validator.validation(context, location, collection)
                    errors.shouldBeNull()
                }
            }

            "when the collection contains a number of elements less than the maximum" - {
                val collection = listOf("A")

                "then the validator should do not return any errors" {
                    val errors = validator.validation(context, location, collection)
                    errors.shouldBeNull()
                }
            }

            "when the collection contains a number of elements equal to the maximum" - {
                val collection = listOf("A", "B")

                "then the validator should do not return any errors" {
                    val errors = validator.validation(context, location, collection)
                    errors.shouldBeNull()
                }
            }

            "when the collection contains a number of elements more than the maximum" - {
                val collection = listOf("A", "B", "C")

                "the validator should return an error" {
                    val errors = validator.validation(context, location, collection)
                    errors.shouldNotBeNull()
                    errors.items shouldContainExactly listOf(
                        JsonErrors.Validation.Arrays.MaxItems(expected = MAX_ITEMS, actual = collection.size)
                    )
                }
            }
        }
    }
}
