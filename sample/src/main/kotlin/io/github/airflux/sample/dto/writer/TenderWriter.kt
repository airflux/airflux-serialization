package io.github.airflux.sample.dto.writer

import io.github.airflux.sample.dto.model.Tender
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.dto.writer.base.PrimitiveWriter.DecimalWriter
import io.github.airflux.sample.dto.writer.base.writer
import io.github.airflux.writer.JsWriter
import io.github.airflux.writer.base.BasePrimitiveWriter

val TenderWriter: JsWriter<Tender> = writer {
    requiredProperty(name = "id", from = Tender::id, writer = BasePrimitiveWriter.string)
    optionalProperty(name = "title", from = Tender::title, writer = BasePrimitiveWriter.string)
    optionalProperty(name = "value", from = Tender::value, writer = ValueWriter)
    optionalProperty(name = "lots", from = Tender::lots, writer = LotsWriter).skipIfEmpty()
}
