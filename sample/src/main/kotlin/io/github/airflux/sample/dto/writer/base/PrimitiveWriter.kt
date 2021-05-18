package io.github.airflux.sample.dto.writer.base

import io.github.airflux.writer.base.BasePrimitiveWriter
import io.github.airflux.writer.base.BasePrimitiveWriter.bigDecimal

object PrimitiveWriter {
    val DecimalWriter = BasePrimitiveWriter.bigDecimal()
}
