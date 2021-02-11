package io.github.airflux.reader.result

import io.github.airflux.common.ObjectContract
import io.github.airflux.value.JsValue
import kotlin.test.Test

class JsErrorTest {

    @Test
    fun `Testing 'equals contract' of the InvalidType class`() {
        ObjectContract.checkEqualsContract(
            JsError.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN),
            JsError.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN),
            JsError.InvalidType(expected = JsValue.Type.BOOLEAN, actual = JsValue.Type.STRING)
        )
    }

    @Test
    fun `Testing 'equals contract' of the NumberFormat class`() {
        ObjectContract.checkEqualsContract(
            JsError.InvalidNumber(value = "abc", type = Int::class),
            JsError.InvalidNumber(value = "abc", type = Int::class),
            JsError.InvalidNumber(value = "", type = Int::class),
        )
    }
}
