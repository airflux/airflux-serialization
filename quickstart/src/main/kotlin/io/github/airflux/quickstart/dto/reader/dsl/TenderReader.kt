package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.dsl.reader.`object`.property.specification.builder.optional
import io.github.airflux.dsl.reader.`object`.property.specification.builder.required
import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.quickstart.dto.reader.dsl.base.reader
import io.github.airflux.quickstart.json.validation.StringValidator.isNotBlank

val TenderReader = reader<Tender> {
    val id = property(required(name = "id", reader = stringReader).validation(isNotBlank))
    val title = property(optional(name = "title", reader = TitleReader))
    val value = property(optional(name = "value", reader = ValueReader))
    val lots = property(required(name = "lots", reader = LotsReader))

    build { location ->
        Tender(
            id = this[id],
            title = this[title],
            value = this[value],
            lots = this[lots],
        ).asSuccess(location)
    }
}
