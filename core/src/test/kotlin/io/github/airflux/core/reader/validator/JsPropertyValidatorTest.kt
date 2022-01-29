package io.github.airflux.core.reader.validator

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.result.JsLocation
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JsPropertyValidatorTest {

    private sealed class ValidationErrors : JsError {
        object PathMissingNormalError : ValidationErrors()
        object InvalidTypeNormalError : ValidationErrors()
    }

    companion object {
        private val context = JsReaderContext()
        private val location = JsLocation.empty
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
        val errors = composeValidator.validation(context, location, Unit)

        assertNull(errors)
    }

    @Test
    fun `Testing operator 'or' (the first validator returns a normal error and the second validator returns a success)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ -> null }

        val composeValidator = leftValidator or rightValidator
        val errors = composeValidator.validation(context, location, Unit)

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
        val errors = composeValidator.validation(context, location, Unit)

        assertNotNull(errors)
        assertEquals(2, errors.items.count())
        assertContains(errors.items, ValidationErrors.PathMissingNormalError)
        assertContains(errors.items, ValidationErrors.InvalidTypeNormalError)
    }

    /*
     * Testing operator 'and'.
     */

    @Test
    fun `Testing operator 'and' (the first validator returns a success and the second validator returns a success)`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ -> null }
        val rightValidator = JsPropertyValidator<Unit> { _, _, _ -> null }

        val composeValidator = leftValidator and rightValidator
        val errors = composeValidator.validation(context, location, Unit)

        assertNull(errors)
    }

    @Test
    fun `Testing operator 'and' (the first validator returns a success and the second validator returns a normal error`() {
        val leftValidator = JsPropertyValidator<Unit> { _, _, _ -> null }

        val rightValidator = JsPropertyValidator<Unit> { _, _, _ ->
            JsErrors.of(ValidationErrors.PathMissingNormalError)
        }

        val composeValidator = leftValidator and rightValidator
        val errors = composeValidator.validation(context, location, Unit)

        assertNotNull(errors)
        assertEquals(1, errors.items.count())
        assertContains(errors.items, ValidationErrors.PathMissingNormalError)
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
        val errors = composeValidator.validation(context, location, Unit)

        assertNotNull(errors)
        assertEquals(1, errors.items.count())
        assertContains(errors.items, ValidationErrors.PathMissingNormalError)
    }
}
