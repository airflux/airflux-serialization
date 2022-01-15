package io.github.airflux.core.writer.extension

import io.github.airflux.core.common.TestData
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.base.buildStringWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteAsRequiredTest {

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

    private class DTO(val firstName: String)
}
