package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.validate
import io.github.airflux.std.validator.comparable.ComparableValidator
import java.math.BigDecimal

private val amountMoreZero = ComparableValidator.gt(BigDecimal.ZERO)

val AmountReader = io.github.airflux.std.reader.BigDecimalReader.validate(amountMoreZero)
