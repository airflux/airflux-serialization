package io.github.airflux.writer.base

import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.writer.JsWriter
import java.math.BigDecimal

@Suppress("unused")
object BasePrimitiveWriter {

    /**
     * Writer for primitive [Boolean] type.
     */
    val boolean: JsWriter<Boolean> = JsWriter { value -> if (value) JsBoolean.True else JsBoolean.False }

    /**
     * Writer for primitive [String] type.
     */
    val string: JsWriter<String> = JsWriter { value -> JsString(value) }

    /**
     * Writer for primitive [Byte] type.
     */
    val byte: JsWriter<Byte> = JsWriter { value -> JsNumber.valueOf(value) }

    /**
     * Writer for primitive [Short] type.
     */
    val short: JsWriter<Short> = JsWriter { value -> JsNumber.valueOf(value) }

    /**
     * Writer for primitive [Int] type.
     */
    val int: JsWriter<Int> = JsWriter { value -> JsNumber.valueOf(value) }

    /**
     * Writer for primitive [Long] type.
     */
    val long: JsWriter<Long> = JsWriter { value -> JsNumber.valueOf(value) }

    /**
     * Writer for primitive [BigDecimal] type.
     */
    fun BasePrimitiveWriter.bigDecimal(
        config: BigDecimalWriterConfiguration = BigDecimalWriterConfiguration()
    ): JsWriter<BigDecimal> = JsWriter { value ->
        value
            .let { if (config.stripTrailingZeros) value.stripTrailingZeros() else value }
            .let { JsNumber.valueOf(it.toPlainString())!! }
    }
}

class BigDecimalWriterConfiguration(
    val stripTrailingZeros: Boolean = false
)
