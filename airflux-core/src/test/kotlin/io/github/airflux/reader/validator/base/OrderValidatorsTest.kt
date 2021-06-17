package io.github.airflux.reader.validator.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsValidationResult
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderValidatorsTest {

    companion object {

        private val context = JsReaderContext()
        private val path = JsResultPath.Root

        private fun minBasicValidator(value: Int) =
            BaseOrderValidators.min<Int, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Min(expected = expectedValue, actual = actualValue)
                }
            )

        private fun maxBasicValidator(value: Int) =
            BaseOrderValidators.max<Int, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Max(expected = expectedValue, actual = actualValue)
                }
            )

        private fun eqBasicValidator(value: Int) =
            BaseOrderValidators.eq<Int, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Eq(expected = expectedValue, actual = actualValue)
                }
            )

        private fun neBasicValidator(value: Int) =
            BaseOrderValidators.ne<Int, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Ne(expected = expectedValue, actual = actualValue)
                }
            )

        private fun gtBasicValidator(value: Int) =
            BaseOrderValidators.gt<Int, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Gt(expected = expectedValue, actual = actualValue)
                }
            )

        private fun ltBasicValidator(value: Int) =
            BaseOrderValidators.lt<Int, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Lt(expected = expectedValue, actual = actualValue)
                }
            )

        private fun geBasicValidator(value: Int) =
            BaseOrderValidators.ge<Int, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Ge(expected = expectedValue, actual = actualValue)
                }
            )

        private fun leBasicValidator(value: Int) =
            BaseOrderValidators.le<Int, JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Numbers.Le(expected = expectedValue, actual = actualValue)
                }
            )
    }

    @Nested
    inner class Min {

        @Test
        fun `Testing basic validator of the 'min' (a number is less than the minimum)`() {
            val minimum = 10
            val actual = 5
            val validator = minBasicValidator(minimum)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Min
            assertEquals(minimum, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'min' (a number is equal to the minimum)`() {
            val minimum = 10
            val actual = 10
            val validator = minBasicValidator(minimum)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'min' (a number is more than the minimum)`() {
            val minimum = 10
            val actual = 15
            val validator = minBasicValidator(minimum)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }
    }

    @Nested
    inner class Max {

        @Test
        fun `Testing basic validator of the 'max' (the number is less than the maximum)`() {
            val maximum = 10
            val actual = 5
            val validator = maxBasicValidator(maximum)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'max' (the number is equal to the maximum)`() {
            val maximum = 10
            val actual = 10
            val validator = maxBasicValidator(maximum)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'max' (the number is more than the maximum)`() {
            val maximum = 10
            val actual = 15
            val validator = maxBasicValidator(maximum)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Max
            assertEquals(maximum, reason.expected)
            assertEquals(actual, reason.actual)
        }
    }

    @Nested
    inner class Eq {

        @Test
        fun `Testing basic validator of the 'eq' (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = eqBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Eq
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'eq' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = eqBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'eq' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = eqBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Eq
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }
    }

    @Nested
    inner class Ne {

        @Test
        fun `Testing basic validator of the 'ne' (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = neBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'ne' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = neBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Ne
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'eq' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = neBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }
    }

    @Nested
    inner class Gt {

        @Test
        fun `Testing basic validator of the 'gt' (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = gtBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Gt
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'gt' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = gtBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Gt
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'gt' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = gtBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }
    }

    @Nested
    inner class Ge {

        @Test
        fun `Testing basic validator of the 'ge' (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = geBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Ge
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'ge' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = geBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'ge' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = geBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }
    }

    @Nested
    inner class Lt {

        @Test
        fun `Testing basic validator of the 'lt' (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = ltBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'lt' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = ltBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Lt
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'lt' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = ltBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Lt
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }
    }

    @Nested
    inner class Le {

        @Test
        fun `Testing basic validator of the 'le' (the number is less than a expected value)`() {
            val expected = 10
            val actual = 5
            val validator = leBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'le' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = leBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'le' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = leBasicValidator(expected)

            val result = validator.validation(context, path, actual)

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Numbers.Le
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }
    }
}
