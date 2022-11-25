package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.quickstart.dto.writer.env.WriterCtx
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.builder.structWriter

val ValueWriter: Writer<WriterCtx, Value> = structWriter {
    property(nonNullable(name = "amount", from = Value::amount, writer = BigDecimalWriter))
    property(nonNullable(name = "currency", from = Value::currency, writer = StringWriter))
}
