package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.reader.base.BigDecimalReader
import io.github.airflux.quickstart.dto.reader.env.ReaderCtx
import io.github.airflux.quickstart.dto.reader.env.ReaderErrorBuilders
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.validation
import io.github.airflux.serialization.core.reader.validator.Validator
import io.github.airflux.serialization.std.validator.comparison.StdComparisonValidator
import java.math.BigDecimal

private val amountMoreZero: Validator<ReaderErrorBuilders, ReaderCtx, BigDecimal> =
    StdComparisonValidator.gt<ReaderErrorBuilders, ReaderCtx, BigDecimal>(BigDecimal.ZERO)

val AmountReader: Reader<ReaderErrorBuilders, ReaderCtx, BigDecimal> = BigDecimalReader.validation(amountMoreZero)
