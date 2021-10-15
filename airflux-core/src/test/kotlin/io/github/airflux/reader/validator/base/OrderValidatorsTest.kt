package io.github.airflux.reader.validator.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OrderValidatorsTest {

    companion object {

        private val context = JsReaderContext()
        private val path = JsResultPath.Root

        private fun minBasicValidator(value: Int) =
            BaseOrderValidators.min(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Min(expected = expectedValue, actual = actualValue)
                }
            )

        private fun maxBasicValidator(value: Int) =
            BaseOrderValidators.max(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Max(expected = expectedValue, actual = actualValue)
                }
            )

        private fun eqBasicValidator(value: Int) =
            BaseOrderValidators.eq(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Eq(expected = expectedValue, actual = actualValue)
                }
            )

        private fun neBasicValidator(value: Int) =
            BaseOrderValidators.ne(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Ne(expected = expectedValue, actual = actualValue)
                }
            )

        private fun gtBasicValidator(value: Int) =
            BaseOrderValidators.gt(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Gt(expected = expectedValue, actual = actualValue)
                }
            )

        private fun ltBasicValidator(value: Int) =
            BaseOrderValidators.lt(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Lt(expected = expectedValue, actual = actualValue)
                }
            )

        private fun geBasicValidator(value: Int) =
            BaseOrderValidators.ge(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Ge(expected = expectedValue, actual = actualValue)
                }
            )

        private fun leBasicValidator(value: Int) =
            BaseOrderValidators.le(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Le(expected = expectedValue, actual = actualValue)
                }
            )
    }

    @Nested
    inner class Min {

        @Test
        fun `Testing the basic validator of the min (a number is less than the minimum)`() {
            val minimum = 10
            val actual = 5
            val validator = minBasicValidator(minimum)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Min(expected = minimum, actual = actual))
        }

        @Test
        fun `Testing the basic validator of the min (a number is equal to the minimum)`() {
            val minimum = 10
            val actual = 10
            val validator = minBasicValidator(minimum)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the min (a number is more than the minimum)`() {
            val minimum = 10
            val actual = 15
            val validator = minBasicValidator(minimum)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }
    }

    @Nested
    inner class Max {

        @Test
        fun `Testing the basic validator of the max (the number is less than the maximum)`() {
            val maximum = 10
            val actual = 5
            val validator = maxBasicValidator(maximum)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the max (the number is equal to the maximum)`() {
            val maximum = 10
            val actual = 10
            val validator = maxBasicValidator(maximum)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the max (the number is more than the maximum)`() {
            val maximum = 10
            val actual = 15
            val validator = maxBasicValidator(maximum)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Max(expected = maximum, actual = actual))
        }
    }

    @Nested
    inner class Eq {

        @Test
        fun `Testing the basic validator of the eq (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = eqBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Eq(expected = expected, actual = actual))
        }

        @Test
        fun `Testing the basic validator of the eq (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = eqBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the eq (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = eqBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Eq(expected = expected, actual = actual))
        }
    }

    @Nested
    inner class Ne {

        @Test
        fun `Testing the basic validator of the ne (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = neBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the ne (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = neBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Ne(expected = expected, actual = actual))
        }

        @Test
        fun `Testing the basic validator of the eq (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = neBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }
    }

    @Nested
    inner class Gt {

        @Test
        fun `Testing the basic validator of the gt (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = gtBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual))
        }

        @Test
        fun `Testing the basic validator of the gt (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = gtBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Gt(expected = expected, actual = actual))
        }

        @Test
        fun `Testing the basic validator of the gt (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = gtBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }
    }

    @Nested
    inner class Ge {

        @Test
        fun `Testing the basic validator of the ge (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = geBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Ge(expected = expected, actual = actual))
        }

        @Test
        fun `Testing the basic validator of the ge (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = geBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the ge (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = geBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }
    }

    @Nested
    inner class Lt {

        @Test
        fun `Testing the basic validator of the lt (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = ltBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the lt (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = ltBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Lt(expected = expected, actual = actual))
        }

        @Test
        fun `Testing the basic validator of the lt (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = ltBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Lt(expected = expected, actual = actual))
        }
    }

    @Nested
    inner class Le {

        @Test
        fun `Testing the basic validator of the le (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = leBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the le (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = leBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the le (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = leBasicValidator(expected)

            val errors = validator.validation(context, path, actual)

            assertNotNull(errors)
            assertEquals(1, errors.count())
            assertContains(errors, JsonErrors.Validation.Numbers.Le(expected = expected, actual = actual))
        }
    }
}
