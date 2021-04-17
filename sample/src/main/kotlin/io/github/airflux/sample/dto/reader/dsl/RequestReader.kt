package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.JsReader
import io.github.airflux.sample.dto.Request
import io.github.airflux.sample.dto.reader.dsl.base.DefaultObjectReaderConfig
import io.github.airflux.sample.dto.reader.dsl.base.DefaultObjectValidations
import io.github.airflux.sample.dto.reader.dsl.base.reader
import io.github.airflux.sample.dto.reader.dsl.base.simpleBuilder

val RequestReader: JsReader<Request> = reader(DefaultObjectReaderConfig, DefaultObjectValidations) {
    val tender = attribute(name = "tender", reader = TenderReader).required()

    typeBuilder = simpleBuilder { values ->
        Request(tender = values[tender])
    }
}
