package io.github.airflux.core.reader.validator.base

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ArrayValidatorsTest {

    companion object {

        private val context = JsReaderContext()
        private val location = JsLocation.empty

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

            val errors = validator.validation(context, location, emptyList())

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Arrays.MinItems(expected = minimum, actual = 0))
        }

        @Test
        fun `Testing basic validator of the minItems (a number of elements is less than the minimum)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val errors = validator.validation(context, location, listOf("A"))

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Arrays.MinItems(expected = minimum, actual = 1))
        }

        @Test
        fun `Testing basic validator of the minItems (a count of elements is equal to the minimum)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val errors = validator.validation(context, location, listOf("A", "B"))

            assertNull(errors)
        }

        @Test
        fun `Testing basic validator of the minItems (a count of elements is more than the minimum)`() {
            val minimum = 2
            val validator = minItemsBasicValidator(minimum)

            val errors = validator.validation(context, location, listOf("A", "B", "C"))

            assertNull(errors)
        }
    }

    @Nested
    inner class MaxItems {

        @Test
        fun `Testing basic validator of the maxItems (a collection is empty)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val errors = validator.validation(context, location, emptyList())

            assertNull(errors)
        }

        @Test
        fun `Testing basic validator of the maxItems (a count of elements is less than the maximum)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val errors = validator.validation(context, location, listOf("A"))

            assertNull(errors)
        }

        @Test
        fun `Testing basic validator of the maxItems (a count of elements is equal to the maximum)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val errors = validator.validation(context, location, listOf("A", "B"))

            assertNull(errors)
        }

        @Test
        fun `Testing basic validator of the maxItems (a count of elements is more than the maximum)`() {
            val maximum = 2
            val validator = maxItemsBasicValidator(maximum)

            val errors = validator.validation(context, location, listOf("A", "B", "C"))

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Arrays.MaxItems(expected = maximum, actual = 3))
        }
    }

    @Nested
    inner class IsUnique {

        @Nested
        inner class FailFastIsTrue {

            @Test
            fun `Testing basic validator of the isUnique (a collection is empty - failFast is true)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = true) { it }

                val errors = validator.validation(context, location, emptyList())

                assertNull(errors)
            }

            @Test
            fun `Testing basic validator of the isUnique (a collection contains only unique values - failFast is true)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = true) { it }

                val errors = validator.validation(context, location, listOf("A", "B"))

                assertNull(errors)
            }

            @Test
            fun `Testing basic validator of the isUnique (a collection contains duplicates - failFast is true)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = true) { it }

                val errors = validator.validation(context, location, listOf("A", "B", "A", "B", "C"))

                assertNotNull(errors)
                assertEquals(1, errors.count())
                assertContains(errors, JsonErrors.Validation.Arrays.Unique(index = 2, value = "A"))
            }
        }

        @Nested
        inner class FailFastIsFalse {

            @Test
            fun `Testing basic validator of the isUnique (a collection is empty - failFast is false)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = false) { it }

                val errors = validator.validation(context, location, emptyList())

                assertNull(errors)
            }

            @Test
            fun `Testing basic validator of the isUnique (a collection contains only unique values - failFast is false)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = false) { it }

                val errors = validator.validation(context, location, listOf("A", "B"))

                assertNull(errors)
            }

            @Test
            fun `Testing basic validator of the isUnique (a collection contains duplicates - failFast is false)`() {
                val validator = isUniqueBasicValidator<String, String>(failFast = false) { it }

                val errors = validator.validation(context, location, listOf("A", "B", "A", "B", "C"))

                assertNotNull(errors)

                assertEquals(2, errors.count())
                assertContains(errors, JsonErrors.Validation.Arrays.Unique(index = 2, value = "A"))
                assertContains(errors, JsonErrors.Validation.Arrays.Unique(index = 3, value = "B"))
            }
        }
    }
}
