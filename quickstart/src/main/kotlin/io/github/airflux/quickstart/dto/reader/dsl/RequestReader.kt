package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.dsl.reader.`object`.property.specification.builder.required
import io.github.airflux.dsl.reader.reader
import io.github.airflux.quickstart.dto.Request

val RequestReader = ObjectReaderScope.reader<Request> {
    val tender = property(required(name = "tender", reader = TenderReader))

    build {
        Request(tender = this[tender]).asSuccess(location)
    }
}
