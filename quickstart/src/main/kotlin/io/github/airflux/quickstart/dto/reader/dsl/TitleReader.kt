package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.reader.base.StringReader
import io.github.airflux.quickstart.dto.reader.base.isNotBlank
import io.github.airflux.quickstart.dto.reader.env.ReaderCtx
import io.github.airflux.quickstart.dto.reader.env.ReaderErrorBuilders
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.validation

val TitleReader: Reader<ReaderErrorBuilders, ReaderCtx, String> = StringReader.validation(isNotBlank)
