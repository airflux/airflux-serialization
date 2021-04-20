package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.sample.dto.Request
import io.github.airflux.sample.dto.reader.dsl.base.reader
import io.github.airflux.sample.dto.reader.dsl.base.simpleBuilder

val RequestReader = reader<Request> {
    val tender = property(name = "tender", reader = TenderReader).required()

    typeBuilder = simpleBuilder { values ->
        Request(tender = values[tender])
    }
}
