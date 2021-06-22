package io.github.airflux.reader.validator.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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

        private val isNotEmptyValidator: JsPropertyValidator<String, JsonErrors.Validation> =
            BaseStringValidators.isNotEmpty { JsonErrors.Validation.Strings.IsEmpty }

        private val isNotBlankValidator: JsPropertyValidator<String, JsonErrors.Validation> =
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

            val error = validator.validation(context, path, "")

            error as JsonErrors.Validation.Strings.MinLength
            assertEquals(minimum, error.expected)
            assertEquals(0, error.actual)
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is blank, and less the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val error = validator.validation(context, path, " ")

            error as JsonErrors.Validation.Strings.MinLength
            assertEquals(minimum, error.expected)
            assertEquals(1, error.actual)
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is blank, and equal the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val error = validator.validation(context, path, "  ")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is blank, and more the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val error = validator.validation(context, path, "   ")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is less than the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val error = validator.validation(context, path, "a")

            error as JsonErrors.Validation.Strings.MinLength
            assertEquals(minimum, error.expected)
            assertEquals(1, error.actual)
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is equal to the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val error = validator.validation(context, path, "ab")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'minLength' (a value of a string is more than the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val error = validator.validation(context, path, "abc")

            assertNull(error)
        }
    }

    @Nested
    inner class MaxLength {

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is empty)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val error = validator.validation(context, path, "")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is blank, and less the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val error = validator.validation(context, path, " ")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is blank, and equal the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val error = validator.validation(context, path, "  ")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is blank, and more the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val error = validator.validation(context, path, "   ")

            error as JsonErrors.Validation.Strings.MaxLength
            assertEquals(maximum, error.expected)
            assertEquals(3, error.actual)
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is less than the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val error = validator.validation(context, path, "a")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is equal to the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val error = validator.validation(context, path, "ab")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'maxLength' (a value of a string is more than the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val error = validator.validation(context, path, "abc")

            error as JsonErrors.Validation.Strings.MaxLength
            assertEquals(maximum, error.expected)
            assertEquals(3, error.actual)
        }
    }

    @Nested
    inner class IsNotEmpty {

        @Test
        fun `Testing basic validator of the 'isNotEmpty' (a value of a string is empty)`() {
            val error = isNotEmptyValidator.validation(context, path, "")

            assertTrue(error is JsonErrors.Validation.Strings.IsEmpty)
        }

        @Test
        fun `Testing basic validator of the 'isNotEmpty' (a value of a string is blank)`() {
            val error = isNotEmptyValidator.validation(context, path, " ")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'isNotEmpty' (a value of a string is not empty)`() {
            val error = isNotEmptyValidator.validation(context, path, "abc")

            assertNull(error)
        }
    }

    @Nested
    inner class IsNotBlank {

        @Test
        fun `Testing basic validator of the 'isNotBlank' (a value of a string is empty)`() {

            val error = isNotBlankValidator.validation(context, path, "")

            assertTrue(error is JsonErrors.Validation.Strings.IsBlank)
        }

        @Test
        fun `Testing basic validator of the 'isNotBlank' (a value of a string is blank)`() {

            val error = isNotBlankValidator.validation(context, path, " ")

            assertTrue(error is JsonErrors.Validation.Strings.IsBlank)
        }

        @Test
        fun `Testing basic validator of the 'isNotBlank' (a value of a string is not blank)`() {

            val error = isNotBlankValidator.validation(context, path, " a ")

            assertNull(error)
        }
    }

    @Nested
    inner class Pattern {
        private val regex = "^abc$".toRegex()
        private val validator = patternBasicValidator(regex)

        @Test
        fun `Testing basic validator of the 'pattern' (a value of a string is empty)`() {
            val error = validator.validation(context, path, "")

            error as JsonErrors.Validation.Strings.Pattern
            assertEquals(regex.pattern, error.regex.pattern)
            assertEquals("", error.value)
        }

        @Test
        fun `Testing basic validator of the 'pattern' (a value of a string is matching to the pattern)`() {
            val error = validator.validation(context, path, "abc")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'pattern' (a value of a string is not matching to the pattern)`() {
            val error = validator.validation(context, path, "aab")

            error as JsonErrors.Validation.Strings.Pattern
            assertEquals(regex.pattern, error.regex.pattern)
            assertEquals("aab", error.value)
        }
    }

    @Nested
    inner class IsA {
        private val patternDigital = "\\d+".toRegex()
        private val isDigital = { value: String -> patternDigital.matches(value) }
        private val validator = isABasicValidator(isDigital)

        @Test
        fun `Testing basic validator of the 'isA' (a value of a string is a number)`() {
            val error = validator.validation(context, path, "123")

            assertNull(error)
        }

        @Test
        fun `Testing basic validator of the 'isA' (a value of a string is not a number)`() {
            val error = validator.validation(context, path, "abc")

            error as JsonErrors.Validation.Strings.IsA
            assertEquals("abc", error.value)
        }

        @Test
        fun `Testing basic validator of the 'isA' (a value of a string is not a number but an empty string)`() {
            val error = validator.validation(context, path, "")

            error as JsonErrors.Validation.Strings.IsA
            assertEquals("", error.value)
        }
    }
}
