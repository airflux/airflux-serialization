package io.github.airflux.std.writer

import io.github.airflux.core.value.JsNumber
import kotlin.test.Test
import kotlin.test.assertEquals

internal class BuilderShortWriterTest {

    companion object {
        private val shortWriter = buildShortWriter()
    }

    @Test
    fun `Testing the writer for the Short type`() {
        val value: Short = Short.MAX_VALUE

        val result = shortWriter.write(value)

        result as JsNumber
        assertEquals(value, result.get.toShort())
    }
}
