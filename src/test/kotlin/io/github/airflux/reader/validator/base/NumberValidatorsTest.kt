package io.github.airflux.reader.validator.base

import io.github.airflux.reader.validator.JsValidationResult
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberValidatorsTest {

    companion object {
        val minBasicValidator = BaseNumberValidators.min<Int, ValidationErrors> { expectedValue, actualValue ->
            ValidationErrors.Numbers.Min(expected = expectedValue, actual = actualValue)
        }

        val maxBasicValidator = BaseNumberValidators.max<Int, ValidationErrors> { expectedValue, actualValue ->
            ValidationErrors.Numbers.Max(expected = expectedValue, actual = actualValue)
        }
    }

    @Nested
    inner class Min {

        @Test
        fun `Testing basic validator of the 'min' (a number is less than the minimum)`() {
            val minimum = 10
            val actual = 5
            val minValueValidator = minBasicValidator(minimum)

            val result = minValueValidator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Min
            assertEquals(minimum, reason.expected)
            assertEquals(actual, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'min' (a number is equal to the minimum)`() {
            val minimum = 10
            val actual = 10
            val minValueValidator = minBasicValidator(minimum)

            val result = minValueValidator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'min' (a number is more than the minimum)`() {
            val minimum = 10
            val actual = 15
            val minValueValidator = minBasicValidator(minimum)

            val result = minValueValidator.validation(actual)

            result as JsValidationResult.Success
        }
    }

    @Nested
    inner class Max {

        @Test
        fun `Testing basic validator of the 'max' (the number is less than the maximum)`() {
            val maximum = 10
            val actual = 5
            val maxValueValidator = maxBasicValidator(maximum)

            val result = maxValueValidator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'max' (the number is equal to the maximum)`() {
            val maximum = 10
            val actual = 10
            val maxValueValidator = maxBasicValidator(maximum)

            val result = maxValueValidator.validation(actual)

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'max' (the number is more than the maximum)`() {
            val maximum = 10
            val actual = 15
            val maxValueValidator = maxBasicValidator(maximum)

            val result = maxValueValidator.validation(actual)

            result as JsValidationResult.Failure
            val reason = result.reason as ValidationErrors.Numbers.Max
            assertEquals(maximum, reason.expected)
            assertEquals(actual, reason.actual)
        }
    }
}
