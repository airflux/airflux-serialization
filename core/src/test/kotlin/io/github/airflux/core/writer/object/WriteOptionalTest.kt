package io.github.airflux.core.writer.`object`

import io.github.airflux.common.TestData
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.dsl.writer.config.ObjectWriterConfig
import io.github.airflux.std.writer.buildStringWriter
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WriteOptionalTest {

    companion object {
        private val objectWriter = ObjectWriter(ObjectWriterConfig.Default)
        private val stringWriter = buildStringWriter()
    }

    @Test
    fun `Testing of a write of an optional property`() {
        val writer = objectWriter<DTO> {
            optionalProperty(name = "name", from = DTO::name, writer = stringWriter)
        }
        val value = DTO(name = TestData.USER_NAME_VALUE)

        val result = writer.write(value)

        result as JsObject
        assertEquals(1, result.count)
        val name = result["name"] as JsString
        assertEquals(TestData.USER_NAME_VALUE, name.get)
    }

    @Test
    fun `Testing of a write of an optional property (a value of a property is null)`() {
        val value = DTO(name = null)
        val writer = objectWriter<DTO> {
            optionalProperty(name = "name", from = DTO::name, writer = stringWriter)
        }

        val result = writer.write(value)

        val expected = JsObject()
        assertEquals(expected, result)
    }

    private data class DTO(val name: String?)
}
