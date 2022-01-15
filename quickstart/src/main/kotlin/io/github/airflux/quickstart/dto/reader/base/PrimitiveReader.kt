package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.core.reader.base.buildStringReader
import io.github.airflux.core.reader.base.buildBigDecimalReader

object PrimitiveReader {
    val stringReader = buildStringReader(ErrorBuilder.InvalidType)
    val bigDecimalReader = buildBigDecimalReader(ErrorBuilder.InvalidType)
}
