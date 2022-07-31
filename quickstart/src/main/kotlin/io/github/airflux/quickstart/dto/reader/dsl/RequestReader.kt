package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.Request
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.reader
import io.github.airflux.serialization.dsl.reader.struct.builder.returns

val RequestReader = reader<Request> {
    val tender = property(required(name = "tender", reader = TenderReader))

    returns { _, location ->
        Request(tender = this[tender]).success(location)
    }
}
