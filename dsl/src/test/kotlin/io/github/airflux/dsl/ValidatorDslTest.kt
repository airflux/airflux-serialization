package io.github.airflux.dsl

import io.github.airflux.dsl.ValidatorDsl.validation
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.ValidationErrors
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidatorDslTest {

    companion object {
        private val isNotEmpty = JsValidator<String, ValidationErrors> {
            if (it.isNotEmpty())
                JsValidationResult.Success
            else
                JsValidationResult.Failure(ValidationErrors.Strings.IsEmpty)
        }
        private val stringReader = JsReader { input -> input.readAsString() }
    }

    @Nested
    inner class Reader {

        @Test
        fun `Testing of the extension-function 'validation' for JsReader`() {
            val path = JsPath("name")
            val json: JsValue = JsObject("name" to JsString("user"))
            val reader = JsReader.required(path = path, stringReader).validation(isNotEmpty)

            val result = reader.read(json)

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals("user", result.value)
        }

        @Test
        fun `Testing of the extension-function 'validation' for JsReader (error of validation)`() {
            val path = JsPath("name")
            val json: JsValue = JsObject("name" to JsString(""))
            val reader = JsReader.required(path = path, stringReader).validation(isNotEmpty)

            val result = reader.read(json)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            val (pathError, errors) = result.errors[0]
            assertEquals(path, pathError)
            assertEquals(1, errors.size)
            assertTrue(errors[0] is ValidationErrors.Strings.IsEmpty)
        }

        @Test
        fun `Testing of the extension-function 'validation' for JsReader (result is failure)`() {
            val path = JsPath("name")
            val json: JsValue = JsObject("name" to JsNull)
            val reader = JsReader.required(path = path, stringReader).validation(isNotEmpty)

            val result = reader.read(json)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            val (pathError, errors) = result.errors[0]
            assertEquals(path, pathError)
            assertEquals(1, errors.size)

            val error = errors[0] as JsError.InvalidType
            assertEquals(JsValue.Type.STRING, error.expected)
            assertEquals(JsValue.Type.NULL, error.actual)
        }
    }

    @Nested
    inner class Result {

        @Test
        fun `Testing of the extension-function 'validation' for JsResult`() {
            val path = JsPath("name")
            val result: JsResult<String> = JsResult.Success(path = path, value = "user")

            val validated = result.validation(isNotEmpty)

            validated as JsResult.Success
            assertEquals(path, validated.path)
            assertEquals("user", validated.value)
        }

        @Test
        fun `Testing of the extension-function 'validation' for JsResult (error of validation)`() {
            val path = JsPath("user")
            val result: JsResult<String> = JsResult.Success(path = path, value = "")

            val validated = result.validation(isNotEmpty)

            validated as JsResult.Failure
            assertEquals(1, validated.errors.size)
            val (pathError, errors) = validated.errors[0]
            assertEquals(path, pathError)
            assertEquals(1, errors.size)
            assertTrue(errors[0] is ValidationErrors.Strings.IsEmpty)
        }

        @Test
        fun `Testing of the extension-function 'validation' for JsResult (result is failure)`() {
            val path = JsPath("user")
            val result: JsResult<String> = JsResult.Failure(path = path, error = JsError.PathMissing)

            val validated = result.validation(isNotEmpty)

            validated as JsResult.Failure
            assertEquals(1, validated.errors.size)
            val (pathError, errors) = validated.errors[0]
            assertEquals(path, pathError)
            assertEquals(1, errors.size)

            assertTrue(errors[0] is JsError.PathMissing)
        }
    }
}
