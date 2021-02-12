package io.github.airflux.value

import io.github.airflux.common.ObjectContract
import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.path.IdxPathElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsArrayTest {

    @Test
    fun `Testing JsArray class without data`() {
        val json = JsArray<JsString>()

        assertTrue(json.underlying.isEmpty())
        assertNull(json[0])
    }

    @Test
    fun `Testing JsArray class`() {

        val json = JsArray(
            JsString(FIRST_PHONE_VALUE),
            JsString(SECOND_PHONE_VALUE),
        )

        assertEquals(2, json.underlying.size)

        val first = json[IdxPathElement(0)]
        first as JsString
        assertEquals(FIRST_PHONE_VALUE, first.underlying)

        val second = json[IdxPathElement(1)]
        second as JsString
        assertEquals(SECOND_PHONE_VALUE, second.underlying)
    }

    @Test
    fun `Testing 'toString' function of the JsArray class`() {
        ObjectContract.checkToString(
            JsArray(
                JsString(FIRST_PHONE_VALUE),
                JsString(SECOND_PHONE_VALUE),
            ),
            """["$FIRST_PHONE_VALUE", "$SECOND_PHONE_VALUE"]"""
        )
    }

    @Test
    fun `Testing 'equals contract' of the JsString class`() {
        ObjectContract.checkEqualsContract(
            JsArray(JsString(FIRST_PHONE_VALUE)),
            JsArray(JsString(FIRST_PHONE_VALUE)),
            JsArray(JsString(SECOND_PHONE_VALUE)),
        )
    }
}
