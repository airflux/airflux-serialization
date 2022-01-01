package io.github.airflux.writer.extension

import io.github.airflux.value.JsArray
import io.github.airflux.value.JsString
import io.github.airflux.writer.base.buildStringWriter
import kotlin.test.Test
import kotlin.test.assertContains

class ArrayWriterTest {

    companion object {
        private val stringWriter = buildStringWriter()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `Testing arrayWriter function`() {
        val writer = arrayWriter(stringWriter)
        val value = listOf("One", "Two")

        val result = writer.write(value)

        result as JsArray<JsString>
        assertContains(result, JsString("One"))
        assertContains(result, JsString("Two"))
    }
}