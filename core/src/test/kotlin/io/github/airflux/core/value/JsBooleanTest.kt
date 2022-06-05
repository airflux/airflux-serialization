package io.github.airflux.core.value

import io.github.airflux.core.common.ObjectContract
import kotlin.test.Test
import kotlin.test.assertEquals

internal class JsBooleanTest {

    @Test
    fun `Testing the toString function of the JsBoolean class`() {
        ObjectContract.checkToString(JsBoolean.valueOf(true), "true")
        ObjectContract.checkToString(JsBoolean.valueOf(false), "false")
    }

    @Test
    fun `Testing inner state of the JsBoolean class`() {
        assertEquals(true, JsBoolean.valueOf(true).get)
        assertEquals(false, JsBoolean.valueOf(false).get)
    }
}
