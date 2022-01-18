package io.github.airflux.core.reader.validator.base//

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.JsPropertyValidator
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ConditionValidatorsTest {

    companion object {
        private val context = JsReaderContext()
        private val location = JsLocation.empty

        private val isNotEmpty: JsPropertyValidator<String> =
            JsPropertyValidator { _, _, value ->
                if (value.isNotEmpty()) null else JsErrors.of(JsonErrors.Validation.Strings.IsEmpty)
            }

        private val validator = isNotEmpty.applyIfNotNull()
    }

    @Test
    fun `Testing the basic validator of the applyIfNotNull (value has string, target validator is apply)`() {
        val errors = validator.validation(context, location, "Hello")

        assertNull(errors)
    }

    @Test
    fun `Testing the basic validator of the applyIfNotNull (value is empty string, target validator is apply)`() {
        val errors = validator.validation(context, location, "")

        assertNotNull(errors)
        assertEquals(1, errors.count())
        assertContains(errors, JsonErrors.Validation.Strings.IsEmpty)
    }

    @Test
    fun `Testing the basic validator of the applyIfNotNull (value is null, target validator do not apply)`() {
        val errors = validator.validation(context, location, null)

        assertNull(errors)
    }

    @Test
    fun `Testing the basic validator of the applyIf (value is empty string, target validator is apply)`() {
        val errors = isNotEmpty.applyIf { _, _, _ -> true }
            .validation(context, location, "")

        assertNotNull(errors)
    }

    @Test
    fun `Testing the basic validator of the applyIf (value is empty string, target validator is not apply)`() {
        val errors = isNotEmpty.applyIf { _, _, _ -> false }
            .validation(context, location, "")

        assertNull(errors)
    }
}
