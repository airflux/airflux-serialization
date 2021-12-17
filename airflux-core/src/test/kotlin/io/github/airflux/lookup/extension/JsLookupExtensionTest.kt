package io.github.airflux.lookup.extension

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.lookup.JsLookup
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.div
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class JsLookupExtensionTest {

    @Nested
    inner class JsValueExtension {

        @Nested
        inner class LookupByKeyPath {

            @Test
            fun `Element by a key is found`() {
                val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))

                val value = json / "name"

                value as JsLookup.Defined
                assertEquals(USER_NAME_VALUE, (value.value as JsString).underlying)
            }

            @Test
            fun `Element by a key is not found`() {
                val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))

                val value = json / "role"

                value as JsLookup.Undefined.PathMissing
                assertEquals("#/role", value.location.toString())
            }

            @Test
            fun `Element is invalid type`() {
                val json: JsValue = JsString(FIRST_PHONE_VALUE)

                val value = json / "name"

                value as JsLookup.Undefined.InvalidType
                assertEquals("#", value.location.toString())
                assertEquals(JsValue.Type.OBJECT, value.expected)
                assertEquals(JsValue.Type.STRING, value.actual)
            }
        }

        @Nested
        inner class LookupByIdxPath {

            @Test
            fun `Element by an index is found`() {
                val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE))

                val value = json / 0

                value as JsLookup.Defined
                assertEquals(FIRST_PHONE_VALUE, (value.value as JsString).underlying)
            }

            @Test
            fun `Element by an index is not found`() {
                val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE))

                val value = json / 1

                value as JsLookup.Undefined.PathMissing
                assertEquals("#[1]", value.location.toString())
            }

            @Test
            fun `Element is invalid type`() {
                val objValue: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))

                val value = objValue / 0

                value as JsLookup.Undefined.InvalidType
                assertEquals("#", value.location.toString())
                assertEquals(JsValue.Type.ARRAY, value.expected)
                assertEquals(JsValue.Type.OBJECT, value.actual)
            }
        }
    }

    @Nested
    inner class JsLookupExtension {

        @Nested
        inner class LookupByKeyPath {

            @Test
            fun `Element by a key is found`() {
                val defined: JsLookup = JsLookup.Defined(
                    location = JsLocation.Root / "user",
                    value = JsObject("name" to JsString(USER_NAME_VALUE))
                )

                val value = defined / "name"

                value as JsLookup.Defined
                assertEquals("#/user/name", value.location.toString())
                assertEquals(USER_NAME_VALUE, (value.value as JsString).underlying)
            }

            @Test
            fun `Element by a key is not found`() {
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

        @Nested
        inner class LookupByIdxPath {

            @Test
            fun `Element by an index is found`() {
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
            fun `Element by an index is not found`() {
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
    }
}
