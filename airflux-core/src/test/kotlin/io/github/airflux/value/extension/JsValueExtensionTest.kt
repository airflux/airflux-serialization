package io.github.airflux.value.extension

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class JsValueExtensionTest {

    @Nested
    inner class Lookup {

        @Test
        fun `testing 'lookup' function (an attribute by name is found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "name" to JsString(USER_NAME_VALUE)
                )
            )
            val path: JsPath = JsPath.empty / "user" / "name"

            val result = json.lookup(path)

            result as JsLookup.Defined
            assertEquals(JsPath.empty / "user" / "name", result.path)
            result.value as JsString
            val value = result.value as JsString
            assertEquals(USER_NAME_VALUE, value.underlying)
        }

        @Test
        fun `testing 'lookup' function (an attribute by name is not found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "name" to JsString(USER_NAME_VALUE)
                )
            )
            val path: JsPath = JsPath.empty / "role" / "name"

            val result = json.lookup(path)

            result as JsLookup.Undefined.PathMissing
            assertEquals(JsPath.empty / "role" / "name", result.path)
        }

        @Test
        fun `testing 'lookup' function (an attribute by name is not found, node is invalid type)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE)
            )
            val path: JsPath = JsPath.empty / "phones" / 0 / "value"

            val result = json.lookup(path)

            result as JsLookup.Undefined.InvalidType
            assertEquals(JsPath.empty, result.path)
            assertEquals(JsValue.Type.OBJECT, result.expected)
            assertEquals(JsValue.Type.ARRAY, result.actual)
        }

        @Test
        fun `testing 'lookup' function (an attribute by index is found)`() {
            val json: JsValue = JsArray(
                JsObject(
                    "value" to JsString(FIRST_PHONE_VALUE)
                )
            )
            val path: JsPath = JsPath.empty / 0 / "value"

            val result = json.lookup(path)

            result as JsLookup.Defined
            assertEquals(JsPath.empty / 0 / "value", result.path)
            result.value as JsString
            val value = result.value as JsString
            assertEquals(FIRST_PHONE_VALUE, value.underlying)
        }

        @Test
        fun `testing 'lookup' function (an attribute by index is not found)`() {
            val json: JsValue = JsArray(
                JsObject(
                    "value" to JsString(FIRST_PHONE_VALUE)
                )
            )
            val path: JsPath = JsPath.empty / 1 / "value"

            val result = json.lookup(path)

            result as JsLookup.Undefined.PathMissing
            assertEquals(JsPath.empty / 1 / "value", result.path)
        }

        @Test
        fun `testing 'lookup' function (an attribute by index is not found, node is invalid type)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
            val path: JsPath = JsPath.empty / 0 / "name"

            val result = json.lookup(path)

            result as JsLookup.Undefined.InvalidType
            assertEquals(JsPath.empty, result.path)
            assertEquals(JsValue.Type.ARRAY, result.expected)
            assertEquals(JsValue.Type.OBJECT, result.actual)
        }
    }
}
