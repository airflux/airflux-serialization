package io.github.airflux.core.value

import io.github.airflux.core.common.ObjectContract
import kotlin.test.Test

internal class JsStringTest {

    @Test
    fun `Testing the toString function of the JsString class`() {
        ObjectContract.checkToString(JsString("user"), "\"user\"")
    }

    @Test
    fun `Testing the equals contract of the JsString class`() {
        ObjectContract.checkEqualsContract(
            JsString("user"),
            JsString("user"),
            JsString("title")
        )
    }
}
