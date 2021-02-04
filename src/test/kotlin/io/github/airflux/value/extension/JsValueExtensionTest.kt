package io.github.airflux.value.extension

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.dsl.PathDsl.div
import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsValueExtensionTest {

    @Nested
    inner class Seek {

        @Test
        fun `Testing extension-function 'seek' of getting an attribute by a path (successful getting of an attribute)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path: JsPath = "user" / "phones" / 0

            val result = json.seek(path)

            result as JsResult.Success
            assertEquals(JsPath(), result.path)
            result.value as JsString
            val value = result.value as JsString
            assertEquals(FIRST_PHONE_VALUE, value.underlying)
        }

        @Test
        fun `Testing extension-function 'seek' of getting an attribute by a path (an attribute is not found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path: JsPath = "user" / "phones" / 1

            val result = json.seek(path)

            result as JsResult.Failure
            val reasons = result.errors
            assertEquals(1, reasons.size)

            val reason = reasons[0]
            assertEquals(JsPath(), reason.first)
            val errors = reason.second
            assertEquals(1, errors.size)
            assertTrue(errors[0] is JsError.PathMissing)
        }
    }

    @Nested
    inner class Lookup {

        @Test
        fun `Testing extension-function 'lookup' of getting an attribute by a path (successful getting of an attribute)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path: JsPath = "user" / "phones" / 0

            val result = json.lookup(path)

            result as JsLookup.Defined
            assertEquals(JsPath(), result.path)
            result.value as JsString
            val value = result.value as JsString
            assertEquals(FIRST_PHONE_VALUE, value.underlying)
        }

        @Test
        fun `Testing extension-function 'lookup' of getting an attribute by a path (an attribute is not found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path: JsPath = "user" / "phones" / 1

            val result = json.lookup(path)

            result as JsLookup.Undefined
            assertEquals(JsPath(), result.path)
            assertTrue(result is JsLookup.Undefined.PathMissing)
        }
    }

    @Nested
    inner class GetByPath {

        @Test
        fun `Testing extension-function 'get' of getting an attribute by a path (successful getting of an attribute)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path: JsPath = "user" / "phones" / 0

            val phone: JsValue? = json.get(path)

            phone as JsString
            assertEquals(FIRST_PHONE_VALUE, phone.underlying)
        }

        @Test
        fun `Testing extension-function 'get' of getting an attribute by a path (an attribute is not found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path: JsPath = "user" / "phones" / 1

            val phone: JsValue? = json.get(path)

            assertNull(phone)
        }
    }

    @Nested
    inner class GetByPathElement {

        @Test
        fun `Testing extension-function 'get' of getting an attribute by a path-element (successful getting of an attribute by a key)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
            val path: PathElement = KeyPathElement("name")

            val name = json.get(path)

            name as JsString
            assertEquals(USER_NAME_VALUE, name.underlying)
        }

        @Test
        fun `Testing extension-function 'get' of getting an attribute by a path-element (successful getting of an attribute by an idx)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE)
            )
            val path: PathElement = IdxPathElement(0)

            val phone = json.get(path)

            phone as JsString
            assertEquals(FIRST_PHONE_VALUE, phone.underlying)
        }
    }

    @Nested
    inner class GetByKeyPathElement {

        @Test
        fun `Testing extension-function 'get' of getting an attribute by a key (successful getting of an attribute by a key)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
            val path = KeyPathElement("name")

            val name: JsValue? = json.get(path)

            name as JsString
            assertEquals(USER_NAME_VALUE, name.underlying)
        }

        @Test
        fun `Testing extension-function 'get' of getting an attribute by a key (an attribute is not found)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
            val path = KeyPathElement("title")

            val title = json.get(path)

            assertNull(title)
        }

        @Test
        fun `Testing extension-function 'get' of getting an attribute by a key (an invalid type of an attribute container)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE)
            )
            val path = KeyPathElement("name")

            val name = json.get(path)

            assertNull(name)
        }
    }

    @Nested
    inner class GetByIdxPathElement {

        @Test
        fun `Testing extension-function 'get' of getting an attribute by an idx (successful getting of an attribute by an idx)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE)
            )
            val path = IdxPathElement(0)

            val phone = json.get(path)

            phone as JsString
            assertEquals(FIRST_PHONE_VALUE, phone.underlying)
        }

        @Test
        fun `Testing extension-function 'get' of getting an attribute by an idx (an attribute is not found)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE)
            )
            val path = IdxPathElement(1)

            val phone = json.get(path)

            assertNull(phone)
        }

        @Test
        fun `Testing extension-function 'get' of getting an attribute by an idx (an invalid type of an attribute container)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
            val path = IdxPathElement(0)

            val name = json.get(path)

            assertNull(name)
        }
    }
}
