package io.github.airflux.reader.validator.base//

import io.github.airflux.common.JsonErrors
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConditionValidatorsTest {

    companion object {
        private val context = JsReaderContext()
        private val path = JsResultPath.Root

        private val isNotEmpty: JsPropertyValidator<String, JsonErrors.Validation.Strings> =
            JsPropertyValidator { _, _, value ->
                if (value.isNotEmpty()) null else JsonErrors.Validation.Strings.IsEmpty
            }

        private val validator = applyIfNotNull(isNotEmpty)
    }

    @Test
    fun `Testing basic validator of the 'applyIfNotNull' (value has string, target validator is apply)`() {
        val error = validator.validation(context, path, "Hello")

        assertNull(error)
    }

    @Test
    fun `Testing basic validator of the 'applyIfNotNull' (value is empty string, target validator is apply)`() {
        val error = validator.validation(context, path, "")

        assertTrue(error is JsonErrors.Validation.Strings.IsEmpty)
    }

    @Test
    fun `Testing basic validator of the 'applyIfNotNull' (value is null, target validator do not apply)`() {
        val error = validator.validation(context, path, null)

        assertNull(error)
    }
}
