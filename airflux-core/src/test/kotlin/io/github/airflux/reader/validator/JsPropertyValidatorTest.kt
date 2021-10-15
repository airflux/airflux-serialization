package io.github.airflux.reader.validator

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsErrors
import io.github.airflux.reader.result.JsResultPath
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
     * Testing operator 'and'.
     */

    @Test
    fun `Testing operator 'or' (the first validator returns a success and the second validator don't execute)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ -> null }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val composeValidator = leftValidator or rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNull(errors)
    }

    @Test
    fun `Testing operator 'or' (the first validator returns a critical error and the second validator don't execute)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.CriticalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val composeValidator = leftValidator or rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNotNull(errors)
        assertEquals(1, errors.count())
        assertContains(errors, ValidationErrors.CriticalError)
    }

    @Test
    fun `Testing operator 'or' (the first validator returns a normal error and the second validator returns a success)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ -> null }

        val composeValidator = leftValidator or rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNull(errors)
    }

    @Test
    fun `Testing operator 'or' (the first validator returns a normal error and the second validator returns a normal error)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.InvalidTypeNormalError)
        }

        val composeValidator = leftValidator or rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNotNull(errors)
        assertEquals(2, errors.count())
        assertContains(errors, ValidationErrors.PathMissingNormalError)
        assertContains(errors, ValidationErrors.InvalidTypeNormalError)
    }

    @Test
    fun `Testing operator 'or' (the first validator returns a normal error and the second validator returns a critical error)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.CriticalError)
        }

        val composeValidator = leftValidator or rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNotNull(errors)
        assertEquals(2, errors.count())
        assertContains(errors, ValidationErrors.PathMissingNormalError)
        assertContains(errors, ValidationErrors.CriticalError)
    }

    /*
     * Testing operator 'and'.
     */

    @Test
    fun `Testing operator 'and' (the first validator returns a success and the second validator returns a success)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ -> null }
        val rightValidator = JsPropertyValidator<Unit> { _, _, _ -> null }

        val composeValidator = leftValidator and rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNull(errors)
    }

    @Test
    fun `Testing operator 'and' (the first validator returns a success and the second validator returns a critical error`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ -> null }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.CriticalError)
        }

        val composeValidator = leftValidator and rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNotNull(errors)
        assertEquals(1, errors.count())
        assertContains(errors, ValidationErrors.CriticalError)
    }

    @Test
    fun `Testing operator 'and' (the first validator returns a success and the second validator returns a normal error`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ -> null }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val composeValidator = leftValidator and rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNotNull(errors)
        assertEquals(1, errors.count())
        assertContains(errors, ValidationErrors.PathMissingNormalError)
    }

    @Test
    fun `Testing operator 'and' (the first validator returns a critical error and the second validator don't execute`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.CriticalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val composeValidator = leftValidator and rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNotNull(errors)
        assertEquals(1, errors.count())
        assertContains(errors, ValidationErrors.CriticalError)
    }

    @Test
    fun `Testing operator 'and' (the first validator returns a normal error and the second validator don't execute`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.InvalidTypeNormalError)
        }

        val composeValidator = leftValidator and rightValidator
        val errors = composeValidator.validation(context, path, Unit)

        assertNotNull(errors)
        assertEquals(1, errors.count())
        assertContains(errors, ValidationErrors.PathMissingNormalError)
    }
}
