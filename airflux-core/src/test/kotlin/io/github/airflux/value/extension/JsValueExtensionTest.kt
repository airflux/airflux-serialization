package io.github.airflux.value.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.JsPath
import io.github.airflux.path.JsPath.Identifiable.Companion.div
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class JsValueExtensionTest {

    companion object {
        private val location = JsLocation.Root / "user"
    }

    @Test
    fun `lookup in json by key`() {
        val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))

        val value = json / "name"

        value as JsLookup.Defined
        assertEquals(USER_NAME_VALUE, (value.value as JsString).underlying)
    }

    @Test
    fun `lookup in json by idx`() {
        val json: JsValue = JsArray(JsString(USER_NAME_VALUE))

        val value = json / 0

        value as JsLookup.Defined
        assertEquals(USER_NAME_VALUE, (value.value as JsString).underlying)
    }

    @Nested
    inner class LookupByPathElement {

        @Nested
        inner class KeyPathElement {

            @Test
            fun defined() {
                val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))
                val pathElement: PathElement = KeyPathElement("name")

                val result = json.lookup(JsLocation.Root, pathElement)

                result as JsLookup.Defined
                assertEquals(JsLocation.Root / "name", result.location)
                result.value as JsString
                val value = result.value as JsString
                assertEquals(USER_NAME_VALUE, value.underlying)
            }

            @Test
            fun `location missing`() {
                val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))
                val pathElement: PathElement = KeyPathElement("user")

                val result = json.lookup(JsLocation.Root, pathElement)

                result as JsLookup.Undefined.PathMissing
                assertEquals(JsLocation.Root / "user", result.location)
            }

            @Test
            fun `invalid type`() {
                val json: JsValue = JsString(USER_NAME_VALUE)
                val pathElement: PathElement = KeyPathElement("user")

                val result = json.lookup(JsLocation.Root, pathElement)

                result as JsLookup.Undefined.InvalidType
                assertEquals(JsLocation.Root, result.location)
                assertEquals(JsValue.Type.OBJECT, result.expected)
                assertEquals(JsValue.Type.STRING, result.actual)
            }
        }

        @Nested
        inner class IdxPathElement {

            @Test
            fun defined() {
                val json: JsValue = JsArray(JsString(USER_NAME_VALUE))
                val pathElement: PathElement = IdxPathElement(0)

                val result = json.lookup(JsLocation.Root, pathElement)

                result as JsLookup.Defined
                assertEquals(JsLocation.Root / 0, result.location)
                result.value as JsString
                val value = result.value as JsString
                assertEquals(USER_NAME_VALUE, value.underlying)
            }

            @Test
            fun `path missing`() {
                val json: JsValue = JsArray(JsString(USER_NAME_VALUE))
                val pathElement: PathElement = IdxPathElement(1)

                val result = json.lookup(JsLocation.Root, pathElement)

                result as JsLookup.Undefined.PathMissing
                assertEquals(JsLocation.Root / 1, result.location)
            }

            @Test
            fun `invalid type`() {
                val json: JsValue = JsString(USER_NAME_VALUE)
                val pathElement: PathElement = IdxPathElement(0)

                val result = json.lookup(JsLocation.Root, pathElement)

                result as JsLookup.Undefined.InvalidType
                assertEquals(JsLocation.Root, result.location)
                assertEquals(JsValue.Type.ARRAY, result.expected)
                assertEquals(JsValue.Type.STRING, result.actual)
            }
        }
    }

    @Nested
    inner class LookupByIdentifiable {

        @Nested
        inner class Simple {

            @Test
            fun defined() {
                val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))
                val path = JsPath.Root / "name"

                val result = json.lookup(JsLocation.Root, path)

                result as JsLookup.Defined
                assertEquals(JsLocation.Root / "name", result.location)
                result.value as JsString
                val value = result.value as JsString
                assertEquals(USER_NAME_VALUE, value.underlying)
            }
        }

        @Nested
        inner class Composite {

            @Test
            fun defined() {
                val json: JsValue = JsObject(
                    "user" to JsObject(
                        "name" to JsString(USER_NAME_VALUE)
                    )
                )
                val path = ("user" / "name") /*as JsLookupPath.Identifiable.Composite*/

                val result = json.lookup(JsLocation.Root, path)

                result as JsLookup.Defined
                assertEquals(JsLocation.Root / "user" / "name", result.location)
                result.value as JsString
                val value = result.value as JsString
                assertEquals(USER_NAME_VALUE, value.underlying)
            }

            @Test
            fun undefined() {
                val json: JsValue = JsObject(
                    "user" to JsObject(
                        "name" to JsString(USER_NAME_VALUE)
                    )
                )
                val path = ("user" / "phones" / 0) as JsPath.Identifiable.Composite

                val result = json.lookup(JsLocation.Root, path)

                result as JsLookup.Undefined
                assertEquals(JsLocation.Root / "user" / "phones", result.location)
            }
        }
    }

    @Nested
    inner class ReadAsBoolean {

        @Test
        fun `Testing the extension-function the readAsBoolean`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsBoolean(location, JsonErrors::InvalidType)

            result.assertAsSuccess(location = location, value = true)
        }

        @Test
        fun `Testing the extension-function the readAsBoolean (invalid type)`() {
            val json = JsString("abc")

            val result = json.readAsBoolean(location, JsonErrors::InvalidType)

            result.assertAsFailure(
                location bind JsonErrors.InvalidType(expected = JsValue.Type.BOOLEAN, actual = JsValue.Type.STRING)
            )
        }
    }

    @Nested
    inner class ReadAsString {
        @Test
        fun `Testing the extension-function the readAsString`() {
            val json: JsValue = JsString("abc")

            val result = json.readAsString(location, JsonErrors::InvalidType)

            result.assertAsSuccess(location = location, value = "abc")
        }

        @Test
        fun `Testing the extension-function the readAsString (invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsString(location, JsonErrors::InvalidType)

            result.assertAsFailure(
                location bind JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
            )
        }
    }

    @Nested
    inner class ReadAsNumber {

        private val transformer = { location: JsLocation, text: String -> JsResult.Success(text.toInt(), location) }

        @Test
        fun `Testing the extension-function the readAsNumber`() {
            val json: JsValue = JsNumber.valueOf(Int.MAX_VALUE)

            val result = json.readAsNumber(location, JsonErrors::InvalidType, transformer)

            result.assertAsSuccess(location = location, value = Int.MAX_VALUE)
        }

        @Test
        fun `Testing the extension-function the readAsNumber invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsNumber(location, JsonErrors::InvalidType, transformer)

            result.assertAsFailure(
                location bind JsonErrors.InvalidType(expected = JsValue.Type.NUMBER, actual = JsValue.Type.BOOLEAN)
            )
        }
    }

    @Nested
    inner class ReadAsObject {

        private val reader = { location: JsLocation, input: JsObject ->
            val userName = input.underlying["name"] as JsString
            JsResult.Success(User(name = userName.underlying), location)
        }

        @Test
        fun `Testing the extension-function the readAsObject`() {
            val json: JsValue = JsObject("name" to JsString("user-name"))

            val result = json.readAsObject(location, JsonErrors::InvalidType, reader)

            result.assertAsSuccess(location = location, value = User(name = "user-name"))
        }

        @Test
        fun `Testing the extension-function the readAsObject invalid type)`() {
            val json: JsValue = JsBoolean.valueOf(true)

            val result = json.readAsObject(location, JsonErrors::InvalidType, reader)

            result.assertAsFailure(
                location bind JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.BOOLEAN)
            )
        }
    }

    internal data class User(val name: String)
}
