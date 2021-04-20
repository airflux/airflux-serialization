package io.github.airflux.value

import io.github.airflux.common.ObjectContract
import io.github.airflux.path.KeyPathElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsObjectTest {

    @Test
    fun `Testing JsObject class without properties`() {
        val json = JsObject()

        assertTrue(json.underlying.isEmpty())
        assertNull(json["name"])
    }

    @Test
    fun `Testing JsObject class`() {
        val userName = "user"
        val isActive = true
        val json = JsObject(
            "name" to JsString(userName),
            "isActive" to JsBoolean.valueOf(isActive)
        )

        assertEquals(2, json.underlying.size)

        val first = json[KeyPathElement("name")]
        first as JsString
        assertEquals(userName, first.underlying)

        val second = json[KeyPathElement("isActive")]
        second as JsBoolean
        assertEquals(isActive, second.underlying)
    }

    @Test
    fun `Testing 'toString' function of the JsObject class`() {
        val userName = "user"
        val isActive = true

        ObjectContract.checkToString(
            JsObject(
                "name" to JsString(userName),
                "isActive" to JsBoolean.valueOf(isActive)
            ),
            """{"name": "$userName", "isActive": $isActive}"""
        )
    }

    @Test
    fun `Testing 'equals contract' of the JsObject class`() {
        val firstUserName = "user-1"
        val secondUserName = "user-2"
        val isActive = true

        ObjectContract.checkEqualsContract(
            JsObject("name" to JsString(firstUserName)),
            JsObject("name" to JsString(secondUserName)),
            JsObject("isActive" to JsBoolean.valueOf(isActive)),
        )
    }
}
