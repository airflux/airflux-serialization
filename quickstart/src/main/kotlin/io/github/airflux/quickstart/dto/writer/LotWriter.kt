package io.github.airflux.quickstart.dto.writer

import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.JsWriter
import io.github.airflux.dsl.writer.array.builder.item.specification.nonNullable
import io.github.airflux.dsl.writer.arrayWriter
import io.github.airflux.dsl.writer.`object`.builder.property.specification.required
import io.github.airflux.dsl.writer.`object`.builder.writer
import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.std.writer.StringWriter

val LotStatus = JsWriter<LotStatus> { _, _, value ->
    JsString(value.name)
}

val LotWriter = writer<Lot> {
    property(required(name = "id", from = Lot::id, StringWriter))
    property(required(name = "status", from = Lot::status, writer = LotStatus))
    property(required(name = "value", from = Lot::value, writer = ValueWriter))
}

val LotsWriter = arrayWriter<Lot> {
    returns(items = nonNullable(LotWriter))
}
