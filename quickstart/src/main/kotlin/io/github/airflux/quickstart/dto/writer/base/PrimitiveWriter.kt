package io.github.airflux.quickstart.dto.writer.base

import io.github.airflux.std.writer.buildBigDecimalWriter
import io.github.airflux.std.writer.buildStringWriter

object PrimitiveWriter {
    val stringWriter = buildStringWriter()
    val DecimalWriter = buildBigDecimalWriter()
}
