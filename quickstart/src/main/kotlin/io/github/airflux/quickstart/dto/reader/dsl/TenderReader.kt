package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.dsl.reader.`object`.property.specification.builder.optional
import io.github.airflux.dsl.reader.`object`.property.specification.builder.required
import io.github.airflux.dsl.reader.objectReaderOf
import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.quickstart.dto.reader.dsl.base.readerBuilderConfig
import io.github.airflux.quickstart.json.validation.StringValidator.isNotBlank

val TenderReader = objectReaderOf<Tender>(readerBuilderConfig) {
    val id = property(required(name = "id", reader = stringReader).validation(isNotBlank))
    val title = property(optional(name = "title", reader = TitleReader))
    val value = property(optional(name = "value", reader = ValueReader))
    val lots = property(required(name = "lots", reader = LotsReader))

    build {
        Tender(+id, +title, +value, +lots).asSuccess(location)
    }
}
