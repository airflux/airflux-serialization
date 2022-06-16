package io.github.airflux.std.writer

import io.github.airflux.core.value.JsNumber
import kotlin.test.Test
import kotlin.test.assertEquals

internal class BuilderByteWriterTest {

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
