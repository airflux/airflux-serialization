package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.validation
import io.github.airflux.serialization.std.reader.StringReader
import io.github.airflux.serialization.std.validator.string.StringValidator.isNotBlank

val TitleReader: Reader<String> = StringReader.validation(isNotBlank)
