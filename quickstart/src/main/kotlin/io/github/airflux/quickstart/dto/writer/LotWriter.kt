package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.writer.base.PrimitiveWriter.stringWriter
import io.github.airflux.quickstart.dto.writer.base.writer
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.JsWriter
import io.github.airflux.core.writer.extension.arrayWriter

val LotStatus = JsWriter<LotStatus> { value ->
    JsString(value.name)
}

val LotWriter = writer<Lot> {
    requiredProperty(name = "id", from = Lot::id, stringWriter)
    requiredProperty(name = "status", from = Lot::status, writer = LotStatus)
    requiredProperty(name = "value", from = Lot::value, writer = ValueWriter)
}

val LotsWriter = arrayWriter(LotWriter)
