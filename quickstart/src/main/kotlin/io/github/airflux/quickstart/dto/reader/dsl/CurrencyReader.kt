package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.quickstart.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.quickstart.json.validation.StringValidator.isNotBlank

val CurrencyReader: JsReader<String> = stringReader.validation(isNotBlank)
