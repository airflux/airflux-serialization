package io.github.airflux.core.writer

import io.github.airflux.common.TestData
import io.github.airflux.core.value.JsString
import io.github.airflux.std.writer.buildStringWriter
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WriteAsRequiredTest {

    companion object {
        private val stringWriter = buildStringWriter()
    }

    @Test
    fun `Testing writeAsRequired function`() {
        val value = DTO(firstName = TestData.USER_NAME_VALUE)

        val result = writeAsRequired(value, DTO::firstName, stringWriter)

        result as JsString
        assertEquals(TestData.USER_NAME_VALUE, result.get)
    }

    private data class DTO(val firstName: String)
}
