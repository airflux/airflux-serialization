package io.github.airflux.core.value

import io.github.airflux.common.ObjectContract
import kotlin.test.Test

internal class JsNullTest {

    @Test
    fun `Testing the toString function of the JsNull class`() {
        ObjectContract.checkToString(JsNull, "null")
    }
}
