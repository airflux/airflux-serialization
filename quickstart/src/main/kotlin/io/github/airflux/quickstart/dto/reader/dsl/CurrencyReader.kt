package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.base.StringReader
import io.github.airflux.core.reader.validator.extension.validation
import io.github.airflux.quickstart.dto.reader.validator.StringValidator.isNotBlank

val CurrencyReader: JsReader<String> = StringReader.validation(isNotBlank)
