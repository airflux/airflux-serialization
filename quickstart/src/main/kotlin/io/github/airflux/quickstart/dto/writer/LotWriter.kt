package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.dsl.writer.array.builder.arrayWriter
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.nullable
import io.github.airflux.serialization.dsl.writer.`object`.builder.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.`object`.builder.writer
import io.github.airflux.serialization.std.writer.StringWriter

val LotStatus = JsWriter<LotStatus> { _, _, value ->
    JsString(value.name)
}

val LotWriter = writer {
    actionIfEmpty = returnNothing()

    property(nonNullable(name = "id", from = Lot::id, StringWriter))
    property(nonNullable(name = "status", from = Lot::status, writer = LotStatus))
    property(nonNullable(name = "value", from = Lot::value, writer = ValueWriter))
}

val LotsWriter = arrayWriter {
    actionIfEmpty = returnNothing()

    items(nullable(LotWriter))
}
