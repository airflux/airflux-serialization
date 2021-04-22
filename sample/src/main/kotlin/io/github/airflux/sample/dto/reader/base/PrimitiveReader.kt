package io.github.airflux.sample.dto.reader.base

import io.github.airflux.reader.base.BasePrimitiveReader

object PrimitiveReader {
    val stringReader = BasePrimitiveReader.string(ErrorBuilder.InvalidType)
    val bigDecimalReader = BasePrimitiveReader.bigDecimal(ErrorBuilder.InvalidType)
}
