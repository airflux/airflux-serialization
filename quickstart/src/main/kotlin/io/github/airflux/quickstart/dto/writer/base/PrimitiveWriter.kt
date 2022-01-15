package io.github.airflux.quickstart.dto.writer.base

import io.github.airflux.core.writer.base.buildBigDecimalWriter
import io.github.airflux.core.writer.base.buildStringWriter

object PrimitiveWriter {
    val stringWriter = buildStringWriter()
    val DecimalWriter = buildBigDecimalWriter()
}
