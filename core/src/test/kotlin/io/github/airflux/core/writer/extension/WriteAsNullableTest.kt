package io.github.airflux.core.writer.extension

import io.github.airflux.common.TestData
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.base.buildStringWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class WriteAsNullableTest {

    companion object {
        private val stringWriter = buildStringWriter()
    }

    @Test
    fun `Testing writeAsNullable function`() {
        val value = DTO(phoneNumber = TestData.FIRST_PHONE_VALUE)

        val result = writeAsNullable(value, DTO::phoneNumber, stringWriter)

        result as JsString
        assertEquals(TestData.FIRST_PHONE_VALUE, result.get)
    }

    @Test
    fun `Testing writeAsNullable function (a value of a property is null)`() {
        val value = DTO()

        val result = writeAsNullable(value, DTO::phoneNumber, stringWriter)

        assertTrue(result is JsNull)
    }

    private data class DTO(val phoneNumber: String? = null)
}
