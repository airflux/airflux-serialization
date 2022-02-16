package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.dsl.reader.`object`.property.specification.builder.required
import io.github.airflux.dsl.reader.objectReaderOf
import io.github.airflux.quickstart.dto.Request
import io.github.airflux.quickstart.dto.reader.dsl.base.readerBuilderConfig

val RequestReader = objectReaderOf<Request>(readerBuilderConfig) {
    val tender = property(required(name = "tender", reader = TenderReader))

    build {
        Request(tender = this[tender]).asSuccess(location)
    }
}
