package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.validation
import io.github.airflux.std.reader.StringReader
import io.github.airflux.std.validator.string.StringValidator.isNotBlank

val TitleReader: JsReader<String> = StringReader.validation(isNotBlank)
