package io.github.airflux.lookup

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import kotlin.test.Test
import kotlin.test.assertEquals

class JsLookupFindByIdxTest {

    @Test
    fun `Element is found`() {
        val defined: JsLookup = JsLookup.Defined(
            location = JsLocation.Root / "user",
            value = JsObject("phones" to JsArray(JsString(FIRST_PHONE_VALUE)))
        )

        val value = defined / "phones" / 0

        value as JsLookup.Defined
        assertEquals("#/user/phones[0]", value.location.toString())
        assertEquals(FIRST_PHONE_VALUE, (value.value as JsString).underlying)
    }

    @Test
    fun `Element is not found`() {
        val defined: JsLookup = JsLookup.Defined(
            location = JsLocation.Root / "user",
            value = JsObject("phones" to JsArray(JsString(FIRST_PHONE_VALUE)))
        )

        val value = defined / "phones" / 1

        value as JsLookup.Undefined.PathMissing
        assertEquals("#/user/phones[1]", value.location.toString())
    }

    @Test
    fun `Element is invalid type`() {
        val defined: JsLookup = JsLookup.Defined(
            location = JsLocation.Root / "name",
            value = JsString(USER_NAME_VALUE)
        )

        val value = defined / 0

        value as JsLookup.Undefined.InvalidType
        assertEquals("#/name", value.location.toString())
        assertEquals(JsValue.Type.ARRAY, value.expected)
        assertEquals(JsValue.Type.STRING, value.actual)
    }

    @Test
    fun `Element is undefined`() {
        val undefined: JsLookup = JsLookup.Undefined.PathMissing(location = JsLocation.Root / "user")

        val value = undefined / 0

        assertEquals(undefined, value)
    }
}
