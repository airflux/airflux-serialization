package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.base.StringReader
import io.github.airflux.core.reader.result.success
import io.github.airflux.dsl.reader.`object`.property.specification.builder.optional
import io.github.airflux.dsl.reader.`object`.property.specification.builder.required
import io.github.airflux.dsl.reader.reader
import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.reader.validator.StringValidator.isNotBlank

val TenderReader = reader<Tender>(ObjectReaderConfiguration) {
    val id = property(required(name = "id", reader = StringReader).validation(isNotBlank))
    val title = property(optional(name = "title", reader = TitleReader))
    val value = property(optional(name = "value", reader = ValueReader))
    val lots = property(required(name = "lots", reader = LotsReader))

    build {
        Tender(+id, +title, +value, +lots).success(location)
    }
}
