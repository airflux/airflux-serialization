package io.github.airflux.reader.validator.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringValidatorsTest {

    companion object {

        private val context = JsReaderContext()
        private val path = JsResultPath.Root

        private fun minLengthBasicValidator(value: Int) =
            BaseStringValidators.minLength<JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Strings.MinLength(expected = expectedValue, actual = actualValue)
                }
            )

        private fun maxLengthBasicValidator(value: Int) =
            BaseStringValidators.maxLength<JsonErrors.Validation>(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Strings.MaxLength(expected = expectedValue, actual = actualValue)
                }
            )

        private val isNotEmptyValidator: JsValidator<String, JsonErrors.Validation> =
            BaseStringValidators.isNotEmpty { JsonErrors.Validation.Strings.IsEmpty }

        private val isNotBlankValidator: JsValidator<String, JsonErrors.Validation> =
            BaseStringValidators.isNotBlank { JsonErrors.Validation.Strings.IsBlank }

        private fun patternBasicValidator(pattern: Regex) =
            BaseStringValidators.pattern<JsonErrors.Validation>(
                pattern = pattern,
                error = { value, regexp -> JsonErrors.Validation.Strings.Pattern(value = value, regex = regexp) }
            )

        private fun isABasicValidator(predicate: (String) -> Boolean) =
            BaseStringValidators.isA<JsonErrors.Validation>(
                predicate = predicate,
                error = { value -> JsonErrors.Validation.Strings.IsA(value = value) }
            )
    }

    @Nested
    inner class MinLength {

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is empty)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val result = validator.validation(context, path, "")

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Strings.MinLength
            assertEquals(minimum, reason.expected)
            assertEquals(0, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is blank, and less the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val result = validator.validation(context, path, " ")

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Strings.MinLength
            assertEquals(minimum, reason.expected)
            assertEquals(1, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is blank, and equal the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val result = validator.validation(context, path, "  ")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is blank, and more the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val result = validator.validation(context, path, "   ")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is less than the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val result = validator.validation(context, path, "a")

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Strings.MinLength
            assertEquals(minimum, reason.expected)
            assertEquals(1, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is equal to the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val result = validator.validation(context, path, "ab")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is more than the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val result = validator.validation(context, path, "abc")

            result as JsValidationResult.Success
        }
    }

    @Nested
    inner class MaxLength {

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is empty)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val result = validator.validation(context, path, "")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is blank, and less the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val result = validator.validation(context, path, " ")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is blank, and equal the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val result = validator.validation(context, path, "  ")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is blank, and more the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val result = validator.validation(context, path, "   ")

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Strings.MaxLength
            assertEquals(maximum, reason.expected)
            assertEquals(3, reason.actual)
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is less than the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val result = validator.validation(context, path, "a")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is equal to the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val result = validator.validation(context, path, "ab")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is more than the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val result = validator.validation(context, path, "abc")

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Strings.MaxLength
            assertEquals(maximum, reason.expected)
            assertEquals(3, reason.actual)
        }
    }

    @Nested
    inner class IsNotEmpty {

        @Test
        fun `Testing basic validator of the 'isNotEmpty' (a value of a string is empty)`() {
            val result = isNotEmptyValidator.validation(context, path, "")

            result as JsValidationResult.Failure
            assertTrue(result.reason is JsonErrors.Validation.Strings.IsEmpty)
        }

        @Test
        fun `Testing basic validator of the 'isNotEmpty' (a value of a string is blank)`() {
            val result = isNotEmptyValidator.validation(context, path, " ")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'isNotEmpty' (a value of a string is not empty)`() {
            val result = isNotEmptyValidator.validation(context, path, "abc")

            result as JsValidationResult.Success
        }
    }

    @Nested
    inner class IsNotBlank {

        @Test
        fun `Testing basic validator of the 'isNotBlank' (a value of a string is empty)`() {

            val result = isNotBlankValidator.validation(context, path, "")

            result as JsValidationResult.Failure
            assertTrue(result.reason is JsonErrors.Validation.Strings.IsBlank)
        }

        @Test
        fun `Testing basic validator of the 'isNotBlank' (a value of a string is blank)`() {

            val result = isNotBlankValidator.validation(context, path, " ")

            result as JsValidationResult.Failure
            assertTrue(result.reason is JsonErrors.Validation.Strings.IsBlank)
        }

        @Test
        fun `Testing basic validator of the 'isNotBlank' (a value of a string is not blank)`() {

            val result = isNotBlankValidator.validation(context, path, " a ")

            result as JsValidationResult.Success
        }
    }

    @Nested
    inner class Pattern {
        private val regex = "^abc$".toRegex()
        private val validator = patternBasicValidator(regex)

        @Test
        fun `Testing basic validator of the 'pattern' (a value of a string is empty)`() {
            val result = validator.validation(context, path, "")

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Strings.Pattern
            assertEquals(regex.pattern, reason.regex.pattern)
            assertEquals("", reason.value)
        }

        @Test
        fun `Testing basic validator of the 'pattern' (a value of a string is matching to the pattern)`() {
            val result = validator.validation(context, path, "abc")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'pattern' (a value of a string is not matching to the pattern)`() {
            val result = validator.validation(context, path, "aab")

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Strings.Pattern
            assertEquals(regex.pattern, reason.regex.pattern)
            assertEquals("aab", reason.value)
        }
    }

    @Nested
    inner class IsA {
        private val patternDigital = "\\d+".toRegex()
        private val isDigital = { value: String -> patternDigital.matches(value) }
        private val validator = isABasicValidator(isDigital)

        @Test
        fun `Testing basic validator of the 'isA' (a value of a string is a number)`() {
            val result = validator.validation(context, path, "123")

            result as JsValidationResult.Success
        }

        @Test
        fun `Testing basic validator of the 'isA' (a value of a string is not a number)`() {
            val result = validator.validation(context, path, "abc")

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Strings.IsA
            assertEquals("abc", reason.value)
        }

        @Test
        fun `Testing basic validator of the 'isA' (a value of a string is not a number but an empty string)`() {
            val result = validator.validation(context, path, "")

            result as JsValidationResult.Failure
            val reason = result.reason as JsonErrors.Validation.Strings.IsA
            assertEquals("", reason.value)
        }
    }
}
