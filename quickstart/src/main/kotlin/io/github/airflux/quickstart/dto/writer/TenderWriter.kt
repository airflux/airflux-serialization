package io.github.airflux.quickstart.dto.writer

import io.github.airflux.dsl.writer.`object`.builder.property.specification.optional
import io.github.airflux.dsl.writer.`object`.builder.property.specification.required
import io.github.airflux.dsl.writer.writer
import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.std.writer.StringWriter

val TenderWriter = writer<Tender> {
    property(required(name = "id", from = Tender::id, writer = StringWriter))
    property(optional(name = "title", from = Tender::title, writer = StringWriter))
    property(optional(name = "value", from = Tender::value, writer = ValueWriter))
    property(optional(name = "lots", from = Tender::lots, writer = LotsWriter))
}
