package io.github.airflux.lookup

import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import kotlin.test.Test
import kotlin.test.assertEquals

class JsLookupFindByKeyTest {

    @Test
    fun `Element is found`() {
        val defined: JsLookup = JsLookup.Defined(
            location = JsLocation.Root / "user",
            value = JsObject("name" to JsString(USER_NAME_VALUE))
        )

        val value = defined / "name"

        value as JsLookup.Defined
        assertEquals("#/user/name", value.location.toString())
        assertEquals(USER_NAME_VALUE, (value.value as JsString).get)
    }

    @Test
    fun `Element is not found`() {
        val defined: JsLookup = JsLookup.Defined(
            location = JsLocation.Root / "user",
            value = JsObject("name" to JsString(USER_NAME_VALUE))
        )

        val value = defined / "type"

        value as JsLookup.Undefined.PathMissing
        assertEquals("#/user/type", value.location.toString())
    }

    @Test
    fun `Element is invalid type`() {
        val defined: JsLookup = JsLookup.Defined(
            location = JsLocation.Root / "user",
            value = JsObject("name" to JsString(USER_NAME_VALUE))
        )

        val value = defined / "name" / "id"

        value as JsLookup.Undefined.InvalidType
        assertEquals("#/user/name", value.location.toString())
        assertEquals(JsValue.Type.OBJECT, value.expected)
        assertEquals(JsValue.Type.STRING, value.actual)
    }

    @Test
    fun `Element is undefined`() {
        val undefined: JsLookup = JsLookup.Undefined.PathMissing(JsLocation.Root / "user")

        val value = undefined / "phones"

        assertEquals(undefined, value)
    }
}
