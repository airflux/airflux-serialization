package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.writer.base.PrimitiveWriter.stringWriter
import io.github.airflux.quickstart.dto.writer.base.writer

val TenderWriter = writer<Tender> {
    requiredProperty(name = "id", from = Tender::id, writer = stringWriter)
    optionalProperty(name = "title", from = Tender::title, writer = stringWriter)
    optionalProperty(name = "value", from = Tender::value, writer = ValueWriter)
    optionalProperty(name = "lots", from = Tender::lots, writer = LotsWriter).skipIfEmpty()
}
