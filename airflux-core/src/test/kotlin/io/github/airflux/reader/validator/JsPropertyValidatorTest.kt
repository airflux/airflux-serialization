package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator.Companion.hasCritical
import io.github.airflux.reader.validator.JsPropertyValidator.Companion.isSuccess
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JsPropertyValidatorTest {

    private sealed class ValidationErrors : JsError {
        object PathMissingNormalError : ValidationErrors()
        object InvalidTypeNormalError : ValidationErrors()
        object CriticalError : ValidationErrors() {
            override val level: JsError.Level = JsError.Level.CRITICAL
        }
    }

    companion object {
        private val context = JsReaderContext()
        private val path = JsResultPath.Root
    }

    /*
     * Testing utility functions.
     */

    @Test
    fun `Testing utility function isSuccess`() {
        val emptyResult = emptyList<ValidationErrors>()
        val resultWithNormalError = listOf(ValidationErrors.PathMissingNormalError)
        val resultWithCriticalError = listOf(ValidationErrors.CriticalError)

        assertTrue(emptyResult.isSuccess())
        assertFalse(resultWithNormalError.isSuccess())
        assertFalse(resultWithCriticalError.isSuccess())
    }

    @Test
    fun `Testing utility function hasCritical`() {
        val emptyResult = emptyList<ValidationErrors>()
        val resultWithNormalError = listOf(ValidationErrors.PathMissingNormalError)
        val resultWithCriticalError = listOf(ValidationErrors.CriticalError)

        assertFalse(emptyResult.hasCritical())
        assertFalse(resultWithNormalError.hasCritical())
        assertTrue(resultWithCriticalError.hasCritical())
    }

    /*
     * Testing operator 'and'.
     */

    @Test
    fun `Testing operator 'or' (the first validator returns a success and the second validator don't execute)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            emptyList()
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.PathMissingNormalError)
        }

        val composeValidator = leftValidator or rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `Testing operator 'or' (the first validator returns a critical error and the second validator don't execute)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.CriticalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.PathMissingNormalError)
        }

        val composeValidator = leftValidator or rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertEquals(1, result.size)
        assertContains(result, ValidationErrors.CriticalError)
    }

    @Test
    fun `Testing operator 'or' (the first validator returns a normal error and the second validator returns a success)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.PathMissingNormalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            emptyList()
        }

        val composeValidator = leftValidator or rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `Testing operator 'or' (the first validator returns a normal error and the second validator returns a normal error)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.PathMissingNormalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.InvalidTypeNormalError)
        }

        val composeValidator = leftValidator or rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertEquals(2, result.size)
        assertContains(result, ValidationErrors.PathMissingNormalError)
        assertContains(result, ValidationErrors.InvalidTypeNormalError)
    }

    @Test
    fun `Testing operator 'or' (the first validator returns a normal error and the second validator returns a critical error)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.PathMissingNormalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.CriticalError)
        }

        val composeValidator = leftValidator or rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertEquals(2, result.size)
        assertContains(result, ValidationErrors.PathMissingNormalError)
        assertContains(result, ValidationErrors.CriticalError)
    }

    /*
     * Testing operator 'and'.
     */

    @Test
    fun `Testing operator 'and' (the first validator returns a success and the second validator returns a success)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            emptyList()
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            emptyList()
        }

        val composeValidator = leftValidator and rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `Testing operator 'and' (the first validator returns a success and the second validator returns a critical error`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            emptyList()
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.CriticalError)
        }

        val composeValidator = leftValidator and rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertEquals(1, result.size)
        assertContains(result, ValidationErrors.CriticalError)
    }

    @Test
    fun `Testing operator 'and' (the first validator returns a success and the second validator returns a normal error`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            emptyList()
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.PathMissingNormalError)
        }

        val composeValidator = leftValidator and rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertEquals(1, result.size)
        assertContains(result, ValidationErrors.PathMissingNormalError)
    }

    @Test
    fun `Testing operator 'and' (the first validator returns a critical error and the second validator don't execute`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.CriticalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.PathMissingNormalError)
        }

        val composeValidator = leftValidator and rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertEquals(1, result.size)
        assertContains(result, ValidationErrors.CriticalError)
    }

    @Test
    fun `Testing operator 'and' (the first validator returns a normal error and the second validator don't execute`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.PathMissingNormalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            listOf(ValidationErrors.InvalidTypeNormalError)
        }

        val composeValidator = leftValidator and rightValidator
        val result = composeValidator.validation(context, path, Unit)

        assertEquals(1, result.size)
        assertContains(result, ValidationErrors.PathMissingNormalError)
    }
}
