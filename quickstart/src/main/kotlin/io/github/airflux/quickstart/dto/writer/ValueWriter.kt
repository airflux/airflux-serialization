package io.github.airflux.quickstart.dto.writer

import io.github.airflux.dsl.writer.`object`.builder.property.specification.required
import io.github.airflux.dsl.writer.writer
import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.std.writer.BigDecimalWriter
import io.github.airflux.std.writer.StringWriter

val ValueWriter = writer<Value> {
    property(required(name = "amount", from = Value::amount, writer = BigDecimalWriter))
    property(required(name = "currency", from = Value::currency, writer = StringWriter))
}
