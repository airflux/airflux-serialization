package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.reader.dsl.property.identifierPropertySpec
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.optional
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.returns
import io.github.airflux.serialization.dsl.reader.struct.builder.structReader

val TenderReader = structReader<Tender>(ObjectReaderConfiguration) {
    val id = property(identifierPropertySpec)
    val title = property(optional(name = "title", reader = TitleReader))
    val value = property(optional(name = "value", reader = ValueReader))
    val lots = property(required(name = "lots", reader = LotsReader))

    returns { _, _ ->
        Tender(+id, +title, +value, +lots).success()
    }
}
