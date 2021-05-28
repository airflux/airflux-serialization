package io.github.airflux.reader.validator.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator
import kotlin.test.Test
import kotlin.test.assertTrue

class ConditionValidatorsTest {

    companion object {
        private val context = JsReaderContext()

        private val isNotEmpty: JsValidator<String, JsonErrors.Validation.Strings> = JsValidator { value, _ ->
            if (value.isNotEmpty())
                JsValidationResult.Success
            else
                JsValidationResult.Failure(JsonErrors.Validation.Strings.IsEmpty)
        }

        private val validator = applyIfNotNull(isNotEmpty)
    }

    @Test
    fun `Testing basic validator of the 'applyIfNotNull' (value has string, target validator is apply)`() {
        val result = validator.validation("Hello", context)

        assertTrue(result is JsValidationResult.Success)
    }

    @Test
    fun `Testing basic validator of the 'applyIfNotNull' (value is empty string, target validator is apply)`() {
        val result = validator.validation("", context)

        result as JsValidationResult.Failure
        assertTrue(result.reason is JsonErrors.Validation.Strings.IsEmpty)
    }

    @Test
    fun `Testing basic validator of the 'applyIfNotNull' (value is null, target validator do not apply)`() {
        val result = validator.validation(null, context)

        assertTrue(result is JsValidationResult.Success)
    }
}
