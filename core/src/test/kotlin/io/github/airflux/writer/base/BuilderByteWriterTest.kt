package io.github.airflux.writer.base

import io.github.airflux.value.JsNumber
import kotlin.test.Test
import kotlin.test.assertEquals

class BuilderByteWriterTest {

    companion object {
        private val byteWriter = buildByteWriter()
    }

    @Test
    fun `Testing the writer for the Byte type`() {
        val value: Byte = Byte.MAX_VALUE

        val result = byteWriter.write(value)

        result as JsNumber
        assertEquals(value, result.get.toByte())
    }
}
