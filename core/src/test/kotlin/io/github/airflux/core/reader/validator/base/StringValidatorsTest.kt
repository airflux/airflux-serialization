package io.github.airflux.core.reader.validator.base

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.JsValidator
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StringValidatorsTest {

    companion object {

        private val context = JsReaderContext()
        private val location = JsLocation.empty

        private fun minLengthBasicValidator(value: Int) =
            BaseStringValidators.minLength(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Strings.MinLength(expected = expectedValue, actual = actualValue)
                }
            )

        private fun maxLengthBasicValidator(value: Int) =
            BaseStringValidators.maxLength(
                expected = value,
                error = { expectedValue, actualValue ->
                    JsonErrors.Validation.Strings.MaxLength(expected = expectedValue, actual = actualValue)
                }
            )

        private val isNotEmptyValidator: JsValidator<String> =
            BaseStringValidators.isNotEmpty { JsonErrors.Validation.Strings.IsEmpty }

        private val isNotBlankValidator: JsValidator<String> =
            BaseStringValidators.isNotBlank { JsonErrors.Validation.Strings.IsBlank }

        private fun patternBasicValidator(pattern: Regex) =
            BaseStringValidators.pattern(
                pattern = pattern,
                error = { value, regexp -> JsonErrors.Validation.Strings.Pattern(value = value, regex = regexp) }
            )

        private fun isABasicValidator(predicate: (String) -> Boolean) =
            BaseStringValidators.isA(
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

            val errors = validator.validation(context, location, "")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.MinLength(expected = minimum, actual = 0))
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is blank, and less the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, location, " ")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.MinLength(expected = minimum, actual = 1))
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is blank, and equal the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, location, "  ")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is blank, and more the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, location, "   ")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is less than the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, location, "a")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.MinLength(expected = minimum, actual = 1))
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is equal to the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, location, "ab")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the minLength (a value of a string is more than the min allowed length)`() {
            val minimum = 2
            val validator = minLengthBasicValidator(minimum)

            val errors = validator.validation(context, location, "abc")

            assertNull(errors)
        }
    }

    @Nested
    inner class MaxLength {

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is empty)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, location, "")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is blank, and less the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, location, " ")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is blank, and equal the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, location, "  ")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is blank, and more the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, location, "   ")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.MaxLength(expected = maximum, actual = 3))
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is less than the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, location, "a")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is equal to the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, location, "ab")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the maxLength (a value of a string is more than the max allowed length)`() {
            val maximum = 2
            val validator = maxLengthBasicValidator(maximum)

            val errors = validator.validation(context, location, "abc")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.MaxLength(expected = maximum, actual = 3))
        }
    }

    @Nested
    inner class IsNotEmpty {

        @Test
        fun `Testing the basic validator of the isNotEmpty (a value of a string is empty)`() {
            val errors = isNotEmptyValidator.validation(context, location, "")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.IsEmpty)
        }

        @Test
        fun `Testing the basic validator of the isNotEmpty (a value of a string is blank)`() {
            val errors = isNotEmptyValidator.validation(context, location, " ")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the isNotEmpty (a value of a string is not empty)`() {
            val errors = isNotEmptyValidator.validation(context, location, "abc")

            assertNull(errors)
        }
    }

    @Nested
    inner class IsNotBlank {

        @Test
        fun `Testing the basic validator of the isNotBlank (a value of a string is empty)`() {

            val errors = isNotBlankValidator.validation(context, location, "")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.IsBlank)
        }

        @Test
        fun `Testing the basic validator of the isNotBlank (a value of a string is blank)`() {

            val errors = isNotBlankValidator.validation(context, location, " ")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.IsBlank)
        }

        @Test
        fun `Testing the basic validator of the isNotBlank (a value of a string is not blank)`() {

            val errors = isNotBlankValidator.validation(context, location, " a ")

            assertNull(errors)
        }
    }

    @Nested
    inner class Pattern {
        private val regex = "^abc$".toRegex()
        private val validator = patternBasicValidator(regex)

        @Test
        fun `Testing the basic validator of the pattern (a value of a string is empty)`() {
            val errors = validator.validation(context, location, "")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.Pattern(value = "", regex = regex))
        }

        @Test
        fun `Testing the basic validator of the pattern (a value of a string is matching to the pattern)`() {
            val errors = validator.validation(context, location, "abc")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the pattern (a value of a string is not matching to the pattern)`() {
            val errors = validator.validation(context, location, "aab")

            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.Pattern(value = "aab", regex = regex))
        }
    }

    @Nested
    inner class IsA {
        private val patternDigital = "\\d+".toRegex()
        private val isDigital = { value: String -> patternDigital.matches(value) }
        private val validator = isABasicValidator(isDigital)

        @Test
        fun `Testing the basic validator of the isA (a value of a string is a number)`() {
            val errors = validator.validation(context, location, "123")

            assertNull(errors)
        }

        @Test
        fun `Testing the basic validator of the isA (a value of a string is not a number)`() {
            val errors = validator.validation(context, location, "abc")

            assertNotNull(errors)
            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.IsA(value = "abc"))
        }

        @Test
        fun `Testing the basic validator of the isA (a value of a string is not a number but an empty string)`() {
            val errors = validator.validation(context, location, "")

            assertNotNull(errors)
            assertNotNull(errors)
            assertEquals(1, errors.items.count())
            assertContains(errors.items, JsonErrors.Validation.Strings.IsA(value = ""))
        }
    }
}
