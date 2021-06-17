package io.github.airflux.reader.validator.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsValidationResult
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayValidatorsTest {

    companion object {

        private val context = JsReaderContext()
        private val path = JsResultPath.Root

        private fun minItemsBasicValidator(value: Int) =
            BaseArrayValidators.minItems<String, List<String>, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Arrays.MinItems(
                        expected = expectedValue,
                        actual = actualValue
                    )
                }
            )

        private fun maxItemsBasicValidator(value: Int) =
            BaseArrayValidators.maxItems<String, List<String>, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Arrays.MaxItems(
                        expected = expectedValue,
                        actual = actualValue
                    )
                }
            )

        fun <T, K> isUniqueBasicValidator(keySelector: (T) -> K) =
            BaseArrayValidators.isUnique<T, K, JsonErrors.Validation>(
                keySelector = keySelector,
                error = { index, value ->
                    JsonErrors.Validation.Arrays.Unique(index = index, value = value)
                }
            )
    }

    @Nested
    inner class MinItems {

        @Test
        fun `Testing basic validator of the 'minItems' (a collection is empty)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val result = validator.validation(context, path, emptyList())

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Arrays.MinItems
            assertEquals(minimum, reason.expected)
            assertEquals(0, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'minItems' (a number of elements is less than the minimum)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val result = validator.validation(context, path, listOf("A"))

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Arrays.MinItems
            assertEquals(minimum, reason.expected)
            assertEquals(1, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'minItems' (a count of elements is equal to the minimum)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val result = validator.validation(context, path, listOf("A", "B"))

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'minItems' (a count of elements is more than the minimum)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val result = validator.validation(context, path, listOf("A", "B", "C"))

            result as JsValidationResult.Success
        }
    }

    @Nested
    inner class MaxItems {

        @Test
        fun `Testing basic validator of the 'maxItems' (a collection is empty)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val result = validator.validation(context, path, emptyList())

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'maxItems' (a count of elements is less than the maximum)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val result = validator.validation(context, path, listOf("A"))

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'maxItems' (a count of elements is equal to the maximum)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val result = validator.validation(context, path, listOf("A", "B"))

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'maxItems' (a count of elements is more than the maximum)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val result = validator.validation(context, path, listOf("A", "B", "C"))

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Arrays.MaxItems
            assertEquals(maximum, reason.expected)
            assertEquals(3, reason.actual)
        }
    }

    @Nested
    inner class IsUnique {

        @Test
        fun `Testing basic validator of the 'isUnique' (a collection is empty)`() {
            val validator = isUniqueBasicValidator<String, String> { it }

            val result = validator.validation(context, path, emptyList())

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'isUnique' (a collection contains only unique values)`() {
            val validator = isUniqueBasicValidator<String, String> { it }

            val result = validator.validation(context, path, listOf("A", "B"))

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'isUnique' (a collection contains duplicates)`() {
            val validator = isUniqueBasicValidator<String, String> { it }

            val result = validator.validation(context, path, listOf("A", "B", "A", "C"))

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Arrays.Unique<*>
            assertEquals(2, reason.index)
            assertEquals("A", reason.value)
        }
    }
}
