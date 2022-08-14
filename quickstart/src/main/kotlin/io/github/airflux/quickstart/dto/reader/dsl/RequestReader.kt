package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.Request
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.returns
import io.github.airflux.serialization.dsl.reader.struct.builder.structReader

val RequestReader = structReader<Request> {
    val tender = property(required(name = "tender", reader = TenderReader))

    returns { _, _ ->
        Request(tender = this[tender]).success()
    }
}
