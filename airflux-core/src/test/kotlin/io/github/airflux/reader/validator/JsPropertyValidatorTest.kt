package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResultPath
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsPropertyValidatorTest {

    private sealed class ValidationErrors : JsError {
        object Error : ValidationErrors()
    }

    companion object {
        private val context = JsReaderContext()
        private val path = JsResultPath.Root
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "true:true",
            "true:false",
            "false:true",
            "false:false",
        ],
        delimiter = ':'
    )
    fun `Testing of the logical operator 'and' of a validator`(left: Boolean, right: Boolean) {
        val leftValidator = JsPropertyValidator<Unit, ValidationErrors> { _, _, _ ->
            if (left) null else ValidationErrors.Error
        }

        val rightValidator = JsPropertyValidator<Unit, ValidationErrors> { _, _, _ ->
            if (right) null else ValidationErrors.Error
        }

        val composeValidator = leftValidator and rightValidator
        val error = composeValidator.validation(context, path, Unit)
        assertEquals(expected = left && right, actual = error == null)
        if (error != null)
            assertTrue(error is ValidationErrors.Error)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "true:true",
            "true:false",
            "false:true",
            "false:false",
        ],
        delimiter = ':'
    )
    fun `Testing of the logical operator 'or' of a validator`(left: Boolean, right: Boolean) {
        val leftValidator = JsPropertyValidator<Unit, ValidationErrors> { _, _, _ ->
            if (left)
                null
            else
                ValidationErrors.Error
        }

        val rightValidator = JsPropertyValidator<Unit, ValidationErrors> { _, _, _ ->
            if (right)
                null
            else
                ValidationErrors.Error
        }

        val composeValidator = leftValidator or rightValidator
        val error = composeValidator.validation(context, path, Unit)

        assertEquals(expected = left || right, actual = error == null)
        if (error != null)
            assertTrue(error is ValidationErrors.Error)
    }
}
