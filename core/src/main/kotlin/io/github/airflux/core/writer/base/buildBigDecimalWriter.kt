package io.github.airflux.core.writer.base

import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.writer.JsWriter
import java.math.BigDecimal

/**
 * Writer for primitive [BigDecimal] type.
 */
fun buildBigDecimalWriter(
    config: BigDecimalWriterConfiguration = BigDecimalWriterConfiguration()
): JsWriter<BigDecimal> = JsWriter { value ->
    value
        .let { if (config.stripTrailingZeros) value.stripTrailingZeros() else value }
        .let { JsNumber.valueOf(it.toPlainString())!! }
}

class BigDecimalWriterConfiguration(
    val stripTrailingZeros: Boolean = false
)
