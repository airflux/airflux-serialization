package io.github.airflux.reader.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class ReadAsExtensionTest {

    @Nested
    inner class ReadAsBoolean {

        @Test
        fun `Testing extension-function 'readAsBoolean'`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsBoolean(JsonErrors::InvalidType)

            result.assertAsSuccess(path = JsPath.empty, value = true)
        }

        @Test
        fun `Testing extension-function 'readAsBoolean' (invalid type)`() {
            val json = JsString("abc")

            val result = json.readAsBoolean(JsonErrors::InvalidType)

            result.assertAsFailure(
                JsPath.empty to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.BOOLEAN, actual = JsValue.Type.STRING)
                )
            )
        }
    }

    @Nested
    inner class ReadAsString {
        @Test
        fun `Testing extension-function 'readAsString'`() {
            val json: JsValue = JsString("abc")

            val result = json.readAsString(JsonErrors::InvalidType)

            result.assertAsSuccess(path = JsPath.empty, value = "abc")
        }

        @Test
        fun `Testing extension-function 'readAsString' (invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsString(JsonErrors::InvalidType)

            result.assertAsFailure(
                JsPath.empty to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                )
            )
        }
    }

    @Nested
    inner class ReadAsNumber {

        private val transformer = { text: String -> JsResult.Success(text.toInt()) }

        @Test
        fun `Testing extension-function 'readAsNumber'`() {
            val json: JsValue = JsNumber.valueOf(Int.MAX_VALUE)

            val result = json.readAsNumber(JsonErrors::InvalidType, transformer)

            result.assertAsSuccess(path = JsPath.empty, value = Int.MAX_VALUE)
        }

        @Test
        fun `Testing extension-function 'readAsNumber' invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsNumber(JsonErrors::InvalidType, transformer)

            result.assertAsFailure(
                JsPath.empty to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.NUMBER, actual = JsValue.Type.BOOLEAN)
                )
            )
        }
    }
}
