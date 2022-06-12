package io.github.airflux.dsl

import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.base.buildStringWriter
import io.github.airflux.dsl.common.TestData
import io.github.airflux.dsl.writer.`object`.ObjectWriter
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WriteRequiredTest {

    companion object {
        private val objectWriter = ObjectWriter(ObjectWriterConfiguration.Default)
        private val stringWriter = buildStringWriter()
    }

    @Test
    fun `Testing of a write of a required property`() {
        val writer = objectWriter<DTO> {
            requiredProperty(name = "name", from = DTO::name, writer = stringWriter)
        }
        val value = DTO(name = TestData.USER_NAME_VALUE)

        val result = writer.write(value)

        result as JsObject
        assertEquals(1, result.count)
        val name = result["name"] as JsString
        assertEquals(TestData.USER_NAME_VALUE, name.get)
    }

    private data class DTO(val name: String)
}
