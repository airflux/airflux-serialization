package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.serialization.core.reader.validation
import io.github.airflux.serialization.std.validator.comparable.ComparableValidator
import java.math.BigDecimal

private val amountMoreZero = ComparableValidator.gt(BigDecimal.ZERO)

val AmountReader = io.github.airflux.serialization.std.reader.BigDecimalReader.validation(amountMoreZero)
