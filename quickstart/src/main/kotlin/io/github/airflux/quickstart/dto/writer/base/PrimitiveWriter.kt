package io.github.airflux.quickstart.dto.writer.base

import io.github.airflux.writer.base.buildBigDecimalWriter
import io.github.airflux.writer.base.buildStringWriter

object PrimitiveWriter {
    val stringWriter = buildStringWriter()
    val DecimalWriter = buildBigDecimalWriter()
}
