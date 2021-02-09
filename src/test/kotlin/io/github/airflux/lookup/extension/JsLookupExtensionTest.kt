package io.github.airflux.lookup.extension

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.dsl.PathDsl.div
import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import kotlin.test.Test
import kotlin.test.assertEquals

class JsLookupExtensionTest {

    @Test
    fun `Testing 'lookup' function (an attribute by a key path element is found)`() {
        val defined = JsLookup.Defined(
            path = JsPath("user"),
            value = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
        )
        val path: PathElement = KeyPathElement("name")

        val result = defined.lookup(path)

        result as JsLookup.Defined
        assertEquals("user" / "name", result.path)
        val value = result.value as JsString
        assertEquals(USER_NAME_VALUE, value.underlying)
    }

    @Test
    fun `Testing 'lookup' function (an attribute by an index path element is found)`() {
        val defined = JsLookup.Defined(
            path = JsPath("phones"),
            value = JsArray(
                JsString(FIRST_PHONE_VALUE)
            )
        )
        val path: PathElement = IdxPathElement(0)

        val result = defined.lookup(path)

        result as JsLookup.Defined
        assertEquals("phones" / 0, result.path)
        val value = result.value as JsString
        assertEquals(FIRST_PHONE_VALUE, value.underlying)
    }

    @Test
    fun `Testing 'lookup' function (an attribute by a name is found)`() {
        val defined = JsLookup.Defined(
            path = JsPath("user"),
            value = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
        )

        val result = defined.lookup(KeyPathElement("name"))

        result as JsLookup.Defined
        assertEquals("user" / "name", result.path)
        val value = result.value as JsString
        assertEquals(USER_NAME_VALUE, value.underlying)
    }

    @Test
    fun `Testing 'lookup' function (an attribute by a name is not found)`() {
        val defined = JsLookup.Defined(
            path = JsPath("user"),
            value = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
        )

        val result = defined.lookup(KeyPathElement("role"))

        result as JsLookup.Undefined.PathMissing
        assertEquals("user" / "role", result.path)
    }

    @Test
    fun `Testing 'lookup' function (an attribute by a name is not found, invalid type)`() {
        val defined = JsLookup.Defined(
            path = JsPath("user"),
            value = JsString(USER_NAME_VALUE)
        )

        val result = defined.lookup(KeyPathElement("name"))

        result as JsLookup.Undefined.InvalidType
        assertEquals(JsPath("user"), result.path)
        assertEquals(JsValue.Type.OBJECT, result.expected)
        assertEquals(JsValue.Type.STRING, result.actual)
    }

    @Test
    fun `Testing 'lookup' function (an attribute by an index is found)`() {
        val defined = JsLookup.Defined(
            path = JsPath("phones"),
            value = JsArray(
                JsString(FIRST_PHONE_VALUE)
            )
        )

        val result = defined.lookup(IdxPathElement(0))

        result as JsLookup.Defined
        assertEquals("phones" / 0, result.path)

        val value = result.value as JsString
        assertEquals(FIRST_PHONE_VALUE, value.underlying)
    }

    @Test
    fun `Testing 'lookup' function (an attribute by an index is not found)`() {
        val defined = JsLookup.Defined(
            path = JsPath("phones"),
            value = JsArray(
                JsString(FIRST_PHONE_VALUE)
            )
        )

        val result = defined.lookup(IdxPathElement(1))

        result as JsLookup.Undefined.PathMissing
        assertEquals("phones" / 1, result.path)
    }

    @Test
    fun `Testing 'lookup' function (an attribute by an index is not found, invalid type)`() {
        val defined = JsLookup.Defined(
            path = JsPath("phones"),
            value = JsString(FIRST_PHONE_VALUE)
        )

        val result = defined.lookup(IdxPathElement(0))

        result as JsLookup.Undefined.InvalidType
        assertEquals(JsPath("phones"), result.path)
        assertEquals(JsValue.Type.ARRAY, result.expected)
        assertEquals(JsValue.Type.STRING, result.actual)
    }
}
