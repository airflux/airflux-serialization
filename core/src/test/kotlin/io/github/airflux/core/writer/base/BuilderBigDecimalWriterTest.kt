package io.github.airflux.core.writer.base

import io.github.airflux.core.value.JsNumber
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class BuilderBigDecimalWriterTest {

    companion object {
        private val value = BigDecimal("10.50")
    }

    @Test
    fun `Testing the writer for the BigDecimal type (default configuration)`() {
        val writer = buildBigDecimalWriter()

        val result = writer.write(value)

        result as JsNumber
        assertEquals(value, BigDecimal(result.get))
    }

    @Test
    fun `Testing the writer for the BigDecimal type (stripTrailingZeros is true)`() {
        val configuration = BigDecimalWriterConfiguration(stripTrailingZeros = true)
        val writer = buildBigDecimalWriter(configuration)

        val result = writer.write(value)

        result as JsNumber
        assertEquals(BigDecimal("10.5"), BigDecimal(result.get))
    }
}
