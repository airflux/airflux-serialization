package io.github.airflux.dsl

import io.github.airflux.common.TestData
import io.github.airflux.dsl.writer.`object`.ObjectWriter
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.writer.base.buildStringWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteRequiredTest {

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
        assertEquals(1, result.underlying.size)
        val name = result.underlying["name"] as JsString
        assertEquals(TestData.USER_NAME_VALUE, name.underlying)
    }

    private class DTO(val name: String)
}
