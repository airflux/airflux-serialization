package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.quickstart.dto.reader.base.PrimitiveReader.bigDecimalReader
import io.github.airflux.quickstart.json.validation.OrderValidator
import java.math.BigDecimal

private val amountMoreZero = OrderValidator.gt(BigDecimal.ZERO)

val AmountReader = bigDecimalReader.validation(amountMoreZero)
