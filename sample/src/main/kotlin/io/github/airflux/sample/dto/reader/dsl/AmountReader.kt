package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.reader.dsl.base.PrimitiveReader.bigDecimalReader
import io.github.airflux.sample.json.validation.NumberValidator
import java.math.BigDecimal

private val amountMoreZero = NumberValidator.gt(BigDecimal.ZERO)

val AmountReader = bigDecimalReader.validation(amountMoreZero)
