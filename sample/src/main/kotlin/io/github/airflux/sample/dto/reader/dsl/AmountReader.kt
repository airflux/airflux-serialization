package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.reader.dsl.base.PrimitiveReader.bigDecimalReader
import io.github.airflux.sample.json.validation.OrderValidator
import java.math.BigDecimal

private val amountMoreZero = OrderValidator.gt(BigDecimal.ZERO)

val AmountReader = bigDecimalReader.validation(amountMoreZero)
