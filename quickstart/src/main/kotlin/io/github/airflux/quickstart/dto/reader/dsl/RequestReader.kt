package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.success
import io.github.airflux.dsl.reader.`object`.property.specification.required
import io.github.airflux.dsl.reader.reader
import io.github.airflux.quickstart.dto.Request

val RequestReader = reader<Request> {
    val tender = property(required(name = "tender", reader = TenderReader))

    returns { _, location ->
        Request(tender = this[tender]).success(location)
    }
}
