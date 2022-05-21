package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.core.reader.base.buildBigDecimalReader
import io.github.airflux.core.reader.base.buildStringReader

object PrimitiveReader {
    val stringReader = buildStringReader()
    val bigDecimalReader = buildBigDecimalReader()
}
