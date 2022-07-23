package io.github.airflux.quickstart.dto.writer

import io.github.airflux.dsl.writer.`object`.builder.property.specification.nonNullable
import io.github.airflux.dsl.writer.`object`.builder.writer
import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.std.writer.BigDecimalWriter
import io.github.airflux.std.writer.StringWriter

val ValueWriter = writer<Value> {
    property(nonNullable(name = "amount", from = Value::amount, writer = BigDecimalWriter))
    property(nonNullable(name = "currency", from = Value::currency, writer = StringWriter))
}
