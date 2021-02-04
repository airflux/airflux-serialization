package io.github.airflux.reader.result.fx

import io.github.airflux.reader.result.JsResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsResultFxTest {

    companion object {
        private const val VALUE = 10
        private const val RESULT_VALUE = 20
    }

    @Test
    fun `Testing 'fx' extension of the JsResult for the binding success`() {
        val function: () -> JsResult<Int> = { JsResult.Failure() }

        val result = JsResult.fx {
            val (value) = function()
            value * 2
        }

        assertTrue(result is JsResult.Failure)
    }

    @Test
    fun `Testing 'fx' extension of the JsResult for the binding failure`() {
        val function: () -> JsResult<Int> = { JsResult.Success(VALUE) }

        val result = JsResult.fx {
            val (value) = function()
            value * 2
        }


        result as JsResult.Success
        assertEquals(RESULT_VALUE, result.value)
    }
}
