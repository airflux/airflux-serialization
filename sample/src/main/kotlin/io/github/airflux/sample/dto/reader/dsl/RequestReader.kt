package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.sample.dto.Request
import io.github.airflux.sample.dto.reader.dsl.base.reader

val RequestReader = reader<Request> {
    val tender = property(name = "tender", reader = TenderReader).required()

    build { values ->
        Request(tender = values[tender])
    }
}
