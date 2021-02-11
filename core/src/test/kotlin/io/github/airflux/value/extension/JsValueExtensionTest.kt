package io.github.airflux.value.extension

import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
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
        fun `Testing 'lookup' function (an attribute by path is found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path: JsPath = JsPath.empty / "user" / "phones" / 0

            val result = json.lookup(path)

            result as JsLookup.Defined
            assertEquals(JsPath.empty / "user" / "phones" / 0, result.path)
            result.value as JsString
            val value = result.value as JsString
            assertEquals(FIRST_PHONE_VALUE, value.underlying)
        }

        @Test
        fun `Testing 'lookup' function (an attribute by path is not found)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "phones" to JsArray(
                        JsString(FIRST_PHONE_VALUE)
                    )
                )
            )
            val path: JsPath = JsPath.empty / "user" / "rules" / 0 / "type"

            val result = json.lookup(path)

            result as JsLookup.Undefined.PathMissing
            assertEquals(JsPath.empty / "user" / "rules", result.path)
        }

        @Test
        fun `Testing 'lookup' function (an attribute by path is not found, invalid type)`() {
            val json: JsValue = JsObject(
                "user" to JsObject(
                    "details" to JsObject(
                        "phones" to JsArray(
                            JsString(FIRST_PHONE_VALUE)
                        )
                    )
                )
            )
            val path: JsPath = JsPath.empty / "user" / "details" / 0 / "phones" / 0

            val result = json.lookup(path)

            result as JsLookup.Undefined.InvalidType
            assertEquals(JsPath.empty / "user" / "details", result.path)
            assertEquals(JsValue.Type.ARRAY, result.expected)
            assertEquals(JsValue.Type.OBJECT, result.actual)
        }
    }
}
