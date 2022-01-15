package io.github.airflux.core.value

import io.github.airflux.core.common.ObjectContract
import kotlin.test.Test

class JsNullTest {

    @Test
    fun `Testing the toString function of the JsNull class`() {
        ObjectContract.checkToString(JsNull, "null")
    }
}
