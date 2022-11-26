package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.writer.env.WriterCtx
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.dsl.writer.array.builder.arrayWriter
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.nullable
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.builder.structWriter

val LotStatus = Writer<WriterCtx, LotStatus> { _, _, value ->
    StringNode(value.name)
}

val LotWriter: Writer<WriterCtx, Lot> = structWriter {
    property(nonNullable(name = "id", from = Lot::id, StringWriter))
    property(nonNullable(name = "status", from = Lot::status, writer = LotStatus))
    property(nonNullable(name = "value", from = Lot::value, writer = ValueWriter))
}

val LotsWriter: Writer<WriterCtx, Iterable<Lot>> = arrayWriter {
    items(nullable(LotWriter))
}
