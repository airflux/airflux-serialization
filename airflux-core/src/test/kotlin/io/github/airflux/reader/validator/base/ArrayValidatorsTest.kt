package io.github.airflux.reader.validator.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArrayValidatorsTest {

    companion object {

        private val context = JsReaderContext()
        private val path = JsResultPath.Root

        private fun minItemsBasicValidator(value: Int) =
            BaseArrayValidators.minItems<String, List<String>>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Arrays.MinItems(
                        expected = expectedValue,
                        actual = actualValue
                    )
                }
            )

        private fun maxItemsBasicValidator(value: Int) =
            BaseArrayValidators.maxItems<String, List<String>>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Arrays.MaxItems(
                        expected = expectedValue,
                        actual = actualValue
                    )
                }
            )

        fun <T, K> isUniqueBasicValidator(failFast: Boolean, keySelector: (T) -> K) =
            BaseArrayValidators.isUnique(
                failFast = failFast,
                keySelector = keySelector,
                error = { index, value ->
                    JsonErrors.Validation.Arrays.Unique(index = index, value = value)
                }
            )
    }

    @Nested
    inner class MinItems {

        @Test
        fun `Testing basic validator of the minItems (a collection is empty)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val errors = validator.validation(context, path, emptyList())

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Arrays.MinItems
            assertEquals(minimum, error.expected)
            assertEquals(0, error.actual)
        }

        @Test
        fun `Testing basic validator of the minItems (a number of elements is less than the minimum)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val errors = validator.validation(context, path, listOf("A"))

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Arrays.MinItems
            assertEquals(minimum, error.expected)
            assertEquals(1, error.actual)
        }

        @Test
        fun `Testing basic validator of the minItems (a count of elements is equal to the minimum)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val errors = validator.validation(context, path, listOf("A", "B"))

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing basic validator of the minItems (a count of elements is more than the minimum)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val errors = validator.validation(context, path, listOf("A", "B", "C"))

            assertTrue(errors.isEmpty())
        }
    }

    @Nested
    inner class MaxItems {

        @Test
        fun `Testing basic validator of the maxItems (a collection is empty)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val errors = validator.validation(context, path, emptyList())

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing basic validator of the maxItems (a count of elements is less than the maximum)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val errors = validator.validation(context, path, listOf("A"))

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing basic validator of the maxItems (a count of elements is equal to the maximum)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val errors = validator.validation(context, path, listOf("A", "B"))

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing basic validator of the maxItems (a count of elements is more than the maximum)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val errors = validator.validation(context, path, listOf("A", "B", "C"))

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Arrays.MaxItems
            assertEquals(maximum, error.expected)
            assertEquals(3, error.actual)
        }
    }

    @Nested
    inner class IsUnique {

        @Nested
        inner class FailFastIsTrue {

            @Test
            fun `Testing basic validator of the isUnique (a collection is empty - failFast is true)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = true) { it }

                val errors = validator.validation(context, path, emptyList())

                assertTrue(errors.isEmpty())
            }

            @Test
            fun `Testing basic validator of the isUnique (a collection contains only unique values - failFast is true)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = true) { it }

                val errors = validator.validation(context, path, listOf("A", "B"))

                assertTrue(errors.isEmpty())
            }

            @Test
            fun `Testing basic validator of the isUnique (a collection contains duplicates - failFast is true)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = true) { it }

                val errors = validator.validation(context, path, listOf("A", "B", "A", "B", "C"))

                assertEquals(1, errors.size)
                val error = errors[0] as JsonErrors.Validation.Arrays.Unique<*>
                assertEquals(2, error.index)
                assertEquals("A", error.value)
            }
        }

        @Nested
        inner class FailFastIsFalse {

            @Test
            fun `Testing basic validator of the isUnique (a collection is empty - failFast is false)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = false) { it }

                val errors = validator.validation(context, path, emptyList())

                assertTrue(errors.isEmpty())
            }

            @Test
            fun `Testing basic validator of the isUnique (a collection contains only unique values - failFast is false)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = false) { it }

                val errors = validator.validation(context, path, listOf("A", "B"))

                assertTrue(errors.isEmpty())
            }

            @Test
            fun `Testing basic validator of the isUnique (a collection contains duplicates - failFast is false)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = false) { it }

                val errors = validator.validation(context, path, listOf("A", "B", "A", "B", "C"))

                assertEquals(2, errors.size)
                errors[0].apply {
                    this as JsonErrors.Validation.Arrays.Unique<*>
                    assertEquals(2, index)
                    assertEquals("A", value)

                }

                errors[1].apply {
                    this as JsonErrors.Validation.Arrays.Unique<*>
                    assertEquals(3, index)
                    assertEquals("B", value)
                }
            }
        }
    }
}
