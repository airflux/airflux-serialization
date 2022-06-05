package io.github.airflux.core.writer.extension

import io.github.airflux.core.common.TestData
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.base.buildStringWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class WriteAsOptionalTest {

    companion object {
        private val stringWriter = buildStringWriter()
    }

    @Test
    fun `Testing writeAsOptional function`() {
        val value = DTO(phoneNumber = TestData.FIRST_PHONE_VALUE)

        val result = writeAsOptional(value, DTO::phoneNumber, stringWriter)

        result as JsString
        assertEquals(TestData.FIRST_PHONE_VALUE, result.get)
    }

    @Test
    fun `Testing writeAsOptional function (a value of a property is null)`() {
        val value = DTO()

        val result = writeAsOptional(value, DTO::phoneNumber, stringWriter)

        assertNull(result)
    }

    private data class DTO(val phoneNumber: String? = null)
}
