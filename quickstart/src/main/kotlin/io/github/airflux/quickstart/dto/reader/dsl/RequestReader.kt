package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.quickstart.dto.Request
import io.github.airflux.quickstart.dto.reader.dsl.base.reader

val RequestReader = reader<Request> {
    val tender = property(name = "tender", reader = TenderReader).required()

    build { location ->
        Request(
            tender = this[tender]
        ).asSuccess(location)
    }
}
