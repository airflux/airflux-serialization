package io.github.airflux.writer.base

import io.github.airflux.value.JsBoolean
import kotlin.test.Test
import kotlin.test.assertEquals

class BuilderBooleanWriterTest {

    companion object {
        private val booleanWriter = buildBooleanWriter()
    }

    @Test
    fun `Testing the writer for the Boolean type (true value)`() {

        val result = booleanWriter.write(true)

        result as JsBoolean
        assertEquals(true, result.underlying)
    }

    @Test
    fun `Testing the writer for the Boolean type (false value)`() {

        val result = booleanWriter.write(false)

        result as JsBoolean
        assertEquals(false, result.underlying)
    }
}
