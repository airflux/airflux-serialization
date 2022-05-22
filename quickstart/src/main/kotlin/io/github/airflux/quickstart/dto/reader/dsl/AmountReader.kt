package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.base.BigDecimalReader
import io.github.airflux.core.reader.validator.extension.validation
import io.github.airflux.quickstart.json.validation.OrderValidator
import java.math.BigDecimal

private val amountMoreZero = OrderValidator.gt(BigDecimal.ZERO)

val AmountReader = BigDecimalReader.validation(amountMoreZero)
