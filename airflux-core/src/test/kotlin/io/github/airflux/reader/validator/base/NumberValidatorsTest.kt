package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsValidationResult
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberValidatorsTest {

    companion object {

        fun minBasicValidator(value: Int) =
            BaseNumberValidators.min<Int, JsError.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    ValidationErrors.Numbers.Min(expected = expectedValue, actual = actualValue)
                }
            )

        fun maxBasicValidator(value: Int) =
            BaseNumberValidators.max<Int, JsError.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    ValidationErrors.Numbers.Max(expected = expectedValue, actual = actualValue)
                }
            )

        fun eqBasicValidator(value: Int) =
            BaseNumberValidators.eq<Int, JsError.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    ValidationErrors.Numbers.Eq(expected = expectedValue, actual = actualValue)
                }
            )

        fun neBasicValidator(value: Int) =
            BaseNumberValidators.ne<Int, JsError.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    ValidationErrors.Numbers.Ne(expected = expectedValue, actual = actualValue)
                }
            )

        fun gtBasicValidator(value: Int) =
            BaseNumberValidators.gt<Int, JsError.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    ValidationErrors.Numbers.Gt(expected = expectedValue, actual = actualValue)
                }
            )

        fun ltBasicValidator(value: Int) =
            BaseNumberValidators.lt<Int, JsError.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    ValidationErrors.Numbers.Lt(expected = expectedValue, actual = actualValue)
                }
            )

        fun geBasicValidator(value: Int) =
            BaseNumberValidators.ge<Int, JsError.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    ValidationErrors.Numbers.Ge(expected = expectedValue, actual = actualValue)
                }
            )

        fun leBasicValidator(value: Int) =
            BaseNumberValidators.le<Int, JsError.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    ValidationErrors.Numbers.Le(expected = expectedValue, actual = actualValue)
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

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Min
            assertEquals(minimum, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'min' (a number is equal to the minimum)`() {
            val minimum = 10
            val actual = 10
            val validator = minBasicValidator(minimum)

            val result = validator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'min' (a number is more than the minimum)`() {
            val minimum = 10
            val actual = 15
            val validator = minBasicValidator(minimum)

            val result = validator.validation(actual)

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

            val result = validator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'max' (the number is equal to the maximum)`() {
            val maximum = 10
            val actual = 10
            val validator = maxBasicValidator(maximum)

            val result = validator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'max' (the number is more than the maximum)`() {
            val maximum = 10
            val actual = 15
            val validator = maxBasicValidator(maximum)

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Max
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

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Eq
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'eq' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = eqBasicValidator(expected)

            val result = validator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'eq' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = eqBasicValidator(expected)

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Eq
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

            val result = validator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'ne' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = neBasicValidator(expected)

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Ne
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'eq' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = neBasicValidator(expected)

            val result = validator.validation(actual)

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

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Gt
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'gt' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = gtBasicValidator(expected)

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Gt
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'gt' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = gtBasicValidator(expected)

            val result = validator.validation(actual)

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

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Ge
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'ge' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = geBasicValidator(expected)

            val result = validator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'ge' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = geBasicValidator(expected)

            val result = validator.validation(actual)

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

            val result = validator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'lt' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = ltBasicValidator(expected)

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Lt
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'lt' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = ltBasicValidator(expected)

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Lt
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

            val result = validator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'le' (the number is equals to a expected value)`() {
            val expected = 10
            val actual = 10
            val validator = leBasicValidator(expected)

            val result = validator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'le' (the number is more than a expected value)`() {
            val expected = 10
            val actual = 15
            val validator = leBasicValidator(expected)

            val result = validator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Le
            assertEquals(expected, reason.expected)
            assertEquals(actual, reason.actual)
        }
    }
}
