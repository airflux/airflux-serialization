package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.base.BigDecimalReader
import io.github.airflux.core.reader.validation
import io.github.airflux.core.reader.validator.std.comparable.ComparableValidator
import java.math.BigDecimal

private val amountMoreZero = ComparableValidator.gt(BigDecimal.ZERO)

val AmountReader = BigDecimalReader.validation(amountMoreZero)
