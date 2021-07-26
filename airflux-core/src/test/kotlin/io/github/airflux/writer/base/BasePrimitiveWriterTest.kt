package io.github.airflux.writer.base

import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.writer.base.BasePrimitiveWriter.bigDecimal
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class BasePrimitiveWriterTest {

    @ParameterizedTest
    @CsvSource(value = ["true", "false"])
    fun `Testing the writer for the Boolean type`(value: Boolean) {

        val result = BasePrimitiveWriter.boolean.write(value)

        result as JsBoolean
        assertEquals(value, result.underlying)
    }

    @Test
    fun `Testing the writer for String type`() {
        val value = "abc"

        val result = BasePrimitiveWriter.string.write(value)

        result as JsString
        assertEquals(value, result.underlying)
    }

    @Test
    fun `Testing the writer for the Byte type`() {
        val value: Byte = Byte.MAX_VALUE

        val result = BasePrimitiveWriter.byte.write(value)

        result as JsNumber
        assertEquals(value, result.underlying.toByte())
    }

    @Test
    fun `Testing the writer for the Short type`() {
        val value: Short = Short.MAX_VALUE

        val result = BasePrimitiveWriter.short.write(value)

        result as JsNumber
        assertEquals(value, result.underlying.toShort())
    }

    @Test
    fun `Testing the writer for the Int type`() {
        val value: Int = Int.MAX_VALUE

        val result = BasePrimitiveWriter.int.write(value)

        result as JsNumber
        assertEquals(value, result.underlying.toInt())
    }

    @Test
    fun `Testing the writer for the Long type`() {
        val value: Long = Long.MAX_VALUE

        val result = BasePrimitiveWriter.long.write(value)

        result as JsNumber
        assertEquals(value, result.underlying.toLong())
    }

    @Test
    fun `Testing the writer for the BigDecimal type (default configuration)`() {
        val value = BigDecimal("10.50")

        val result = BasePrimitiveWriter.bigDecimal().write(value)

        result as JsNumber
        assertEquals(value, BigDecimal(result.underlying))
    }

    @Test
    fun `Testing the writer for the BigDecimal type (stripTrailingZeros is true)`() {
        val value = BigDecimal("10.50")
        val configuration = BigDecimalWriterConfiguration(stripTrailingZeros = true)

        val result = BasePrimitiveWriter.bigDecimal(configuration).write(value)

        result as JsNumber
        assertEquals(BigDecimal("10.5"), BigDecimal(result.underlying))
    }
}
