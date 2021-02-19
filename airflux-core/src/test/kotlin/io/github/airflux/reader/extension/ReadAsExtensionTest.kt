package io.github.airflux.reader.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadAsExtensionTest {

    @Nested
    inner class ReadAsBoolean {

        @Test
        fun `Testing extension-function 'readAsBoolean'`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsBoolean(JsonErrors::InvalidType)

            result as JsResult.Success
            assertEquals(true, result.value)
        }

        @Test
        fun `Testing extension-function 'readAsBoolean' (invalid type)`() {
            val json = JsString("abc")

            val result = json.readAsBoolean(JsonErrors::InvalidType)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsonErrors.InvalidType
            assertEquals(JsValue.Type.BOOLEAN, error.expected)
            assertEquals(JsValue.Type.STRING, error.actual)
        }
    }

    @Nested
    inner class ReadAsString {
        @Test
        fun `Testing extension-function 'readAsString'`() {
            val json: JsValue = JsString("abc")

            val result = json.readAsString(JsonErrors::InvalidType)

            result as JsResult.Success
            assertEquals("abc", result.value)
        }

        @Test
        fun `Testing extension-function 'readAsString' (invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsString(JsonErrors::InvalidType)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsonErrors.InvalidType
            assertEquals(JsValue.Type.STRING, error.expected)
            assertEquals(JsValue.Type.BOOLEAN, error.actual)
        }
    }

    @Nested
    inner class ReadAsNumber {

        private val transformer = { text: String -> JsResult.Success(text.toInt()) }

        @Test
        fun `Testing extension-function 'readAsNumber'`() {
            val json: JsValue = JsNumber.valueOf(Int.MAX_VALUE)

            val result = json.readAsNumber(JsonErrors::InvalidType, transformer)

            result as JsResult.Success
            assertEquals(Int.MAX_VALUE, result.value)
        }

        @Test
        fun `Testing extension-function 'readAsNumber' invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsNumber(JsonErrors::InvalidType, transformer)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            val (path, errors) = result.errors[0]
            assertEquals(JsPath.empty, path)
            assertEquals(1, errors.size)

            val error = errors[0] as JsonErrors.InvalidType
            assertEquals(JsValue.Type.NUMBER, error.expected)
            assertEquals(JsValue.Type.BOOLEAN, error.actual)
        }
    }
}
