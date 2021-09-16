package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.writer.base.writer
import io.github.airflux.writer.base.BasePrimitiveWriter

val TenderWriter = writer<Tender> {
    requiredProperty(name = "id", from = Tender::id, writer = BasePrimitiveWriter.string)
    optionalProperty(name = "title", from = Tender::title, writer = BasePrimitiveWriter.string)
    optionalProperty(name = "value", from = Tender::value, writer = ValueWriter)
    optionalProperty(name = "lots", from = Tender::lots, writer = LotsWriter).skipIfEmpty()
}
