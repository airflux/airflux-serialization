package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.reader.dsl.base.PrimitiveReader.stringReader
import io.github.airflux.sample.json.validation.StringValidator.isNotBlank

val CurrencyReader = stringReader.validation(isNotBlank)
