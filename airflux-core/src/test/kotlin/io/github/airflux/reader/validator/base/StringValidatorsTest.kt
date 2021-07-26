package io.github.airflux.reader.validator.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator
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
        fun `Testing the basic validator of the minLength (a value of a string is empty)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, path, "")

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Strings.MinLength
            assertEquals(minimum, error.expected)
            assertEquals(0, error.actual)
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is blank, and less the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, path, " ")

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Strings.MinLength
            assertEquals(minimum, error.expected)
            assertEquals(1, error.actual)
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is blank, and equal the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, path, "  ")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is blank, and more the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, path, "   ")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is less than the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, path, "a")

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Strings.MinLength
            assertEquals(minimum, error.expected)
            assertEquals(1, error.actual)
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is equal to the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, path, "ab")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is more than the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, path, "abc")

            assertTrue(errors.isEmpty())
        }
    }

    @Nested
    inner class MaxLength {

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is empty)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, path, "")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is blank, and less the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, path, " ")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is blank, and equal the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, path, "  ")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is blank, and more the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, path, "   ")

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Strings.MaxLength
            assertEquals(maximum, error.expected)
            assertEquals(3, error.actual)
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is less than the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, path, "a")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is equal to the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, path, "ab")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is more than the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, path, "abc")

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Strings.MaxLength
            assertEquals(maximum, error.expected)
            assertEquals(3, error.actual)
        }
    }

    @Nested
    inner class IsNotEmpty {

        @Test
        fun `Testing the basic validator of the isNotEmpty (a value of a string is empty)`() {
            val errors = isNotEmptyValidator.validation(context, path, "")

            assertEquals(1, errors.size)
            assertTrue(errors[0] is JsonErrors.Validation.Strings.IsEmpty)
        }

        @Test
        fun `Testing the basic validator of the isNotEmpty (a value of a string is blank)`() {
            val errors = isNotEmptyValidator.validation(context, path, " ")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the isNotEmpty (a value of a string is not empty)`() {
            val errors = isNotEmptyValidator.validation(context, path, "abc")

            assertTrue(errors.isEmpty())
        }
    }

    @Nested
    inner class IsNotBlank {

        @Test
        fun `Testing the basic validator of the isNotBlank (a value of a string is empty)`() {

            val errors = isNotBlankValidator.validation(context, path, "")

            assertEquals(1, errors.size)
            assertTrue(errors[0] is JsonErrors.Validation.Strings.IsBlank)
        }

        @Test
        fun `Testing the basic validator of the isNotBlank (a value of a string is blank)`() {

            val errors = isNotBlankValidator.validation(context, path, " ")

            assertEquals(1, errors.size)
            assertTrue(errors[0] is JsonErrors.Validation.Strings.IsBlank)
        }

        @Test
        fun `Testing the basic validator of the isNotBlank (a value of a string is not blank)`() {

            val errors = isNotBlankValidator.validation(context, path, " a ")

            assertTrue(errors.isEmpty())
        }
    }

    @Nested
    inner class Pattern {
        private val regex = "^abc$".toRegex()
        private val validator = patternBasicValidator(regex)

        @Test
        fun `Testing the basic validator of the pattern (a value of a string is empty)`() {
            val errors = validator.validation(context, path, "")

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Strings.Pattern
            assertEquals(regex.pattern, error.regex.pattern)
            assertEquals("", error.value)
        }

        @Test
        fun `Testing the basic validator of the pattern (a value of a string is matching to the pattern)`() {
            val errors = validator.validation(context, path, "abc")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the pattern (a value of a string is not matching to the pattern)`() {
            val errors = validator.validation(context, path, "aab")

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Strings.Pattern
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
        fun `Testing the basic validator of the isA (a value of a string is a number)`() {
            val errors = validator.validation(context, path, "123")

            assertTrue(errors.isEmpty())
        }

        @Test
        fun `Testing the basic validator of the isA (a value of a string is not a number)`() {
            val errors = validator.validation(context, path, "abc")

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Strings.IsA
            assertEquals("abc", error.value)
        }

        @Test
        fun `Testing the basic validator of the isA (a value of a string is not a number but an empty string)`() {
            val errors = validator.validation(context, path, "")

            assertEquals(1, errors.size)
            val error = errors[0] as JsonErrors.Validation.Strings.IsA
            assertEquals("", error.value)
        }
    }
}
