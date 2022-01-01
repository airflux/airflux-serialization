package io.github.airflux.dsl

import io.github.airflux.common.TestData
import io.github.airflux.dsl.writer.`object`.ObjectWriter
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.writer.base.buildStringWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteNullableTest {

    companion object {
        private val objectWriter = ObjectWriter(ObjectWriterConfiguration.Default)
        private val stringWriter = buildStringWriter()
    }

    @Test
    fun `Testing of a write of a nullable property`() {
        val value = DTO(name = TestData.USER_NAME_VALUE)
        val writer = objectWriter<DTO> {
            nullableProperty(name = "name", from = DTO::name, writer = stringWriter)
        }

        val result = writer.write(value)

        result as JsObject
        assertEquals(1, result.count)
        val name = result["name"] as JsString
        assertEquals(TestData.USER_NAME_VALUE, name.get)
    }

    @Test
    fun `Testing of a write of a nullable property (a value of a property is null)`() {
        val value = DTO(name = null)
        val writer = objectWriter<DTO> {
            nullableProperty(name = "name", from = DTO::name, writer = stringWriter)
        }

        val result = writer.write(value)

        result as JsObject
        assertEquals(1, result.count)
        assertEquals(JsNull, result["name"])
    }

    private class DTO(val name: String?)
}
