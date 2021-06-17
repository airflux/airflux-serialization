package io.github.airflux.reader.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class ReadAsExtensionTest {

    companion object {
        private val currentPath = JsResultPath.Root / "user"
    }

    @Nested
    inner class ReadAsBoolean {

        @Test
        fun `Testing extension-function 'readAsBoolean'`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsBoolean(currentPath, JsonErrors::InvalidType)

            result.assertAsSuccess(path = currentPath, value = true)
        }

        @Test
        fun `Testing extension-function 'readAsBoolean' (invalid type)`() {
            val json = JsString("abc")

            val result = json.readAsBoolean(currentPath, JsonErrors::InvalidType)

            result.assertAsFailure(
                currentPath to listOf(
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

            val result = json.readAsString(currentPath, JsonErrors::InvalidType)

            result.assertAsSuccess(path = currentPath, value = "abc")
        }

        @Test
        fun `Testing extension-function 'readAsString' (invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsString(currentPath, JsonErrors::InvalidType)

            result.assertAsFailure(
                currentPath to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                )
            )
        }
    }

    @Nested
    inner class ReadAsNumber {

        private val transformer = { path: JsResultPath, text: String -> JsResult.Success(text.toInt(), path) }

        @Test
        fun `Testing extension-function 'readAsNumber'`() {
            val json: JsValue = JsNumber.valueOf(Int.MAX_VALUE)

            val result = json.readAsNumber(currentPath, JsonErrors::InvalidType, transformer)

            result.assertAsSuccess(path = currentPath, value = Int.MAX_VALUE)
        }

        @Test
        fun `Testing extension-function 'readAsNumber' invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsNumber(currentPath, JsonErrors::InvalidType, transformer)

            result.assertAsFailure(
                currentPath to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.NUMBER, actual = JsValue.Type.BOOLEAN)
                )
            )
        }
    }

    data class User(val name: String)

    @Nested
    inner class ReadAsObject {

        private val reader = { path: JsResultPath, input: JsObject ->
            val userName = input.underlying["name"] as JsString
            JsResult.Success(User(name = userName.underlying), path)
        }

        @Test
        fun `Testing extension-function 'readAsObject'`() {
            val json: JsValue = JsObject("name" to JsString("user-name"))

            val result = json.readAsObject(currentPath, JsonErrors::InvalidType, reader)

            result.assertAsSuccess(path = currentPath, value = User(name = "user-name"))
        }

        @Test
        fun `Testing extension-function 'readAsObject' invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsObject(currentPath, JsonErrors::InvalidType, reader)

            result.assertAsFailure(
                currentPath to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.BOOLEAN)
                )
            )
        }
    }
}
