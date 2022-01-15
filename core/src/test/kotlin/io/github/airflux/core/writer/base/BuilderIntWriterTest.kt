package io.github.airflux.core.writer.base

import io.github.airflux.core.value.JsNumber
import kotlin.test.Test
import kotlin.test.assertEquals

class BuilderIntWriterTest {

    companion object {
        private val intWriter = buildIntWriter()
    }

    @Test
    fun `Testing the writer for the Int type`() {
        val value: Int = Int.MAX_VALUE

        val result = intWriter.write(value)

        result as JsNumber
        assertEquals(value, result.get.toInt())
    }
}
