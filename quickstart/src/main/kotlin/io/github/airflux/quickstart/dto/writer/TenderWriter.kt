package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.writer.env.WriterCtx
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.optional
import io.github.airflux.serialization.dsl.writer.struct.builder.structWriter

val TenderWriter: Writer<WriterCtx, Tender> = structWriter {
    property(nonNullable(name = "id", from = Tender::id, writer = StringWriter))
    property(optional(name = "title", from = Tender::title, writer = StringWriter))
    property(optional(name = "value", from = Tender::value, writer = ValueWriter))
    property(optional(name = "lots", from = Tender::lots, writer = LotsWriter))
}
