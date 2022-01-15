package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.validator.extension.validation
import io.github.airflux.quickstart.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.quickstart.json.validation.StringValidator.isNotBlank

val TitleReader: JsReader<String> = stringReader.validation(isNotBlank)
