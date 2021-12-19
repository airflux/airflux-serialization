package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.quickstart.dto.writer.base.PrimitiveWriter.DecimalWriter
import io.github.airflux.quickstart.dto.writer.base.PrimitiveWriter.stringWriter
import io.github.airflux.quickstart.dto.writer.base.writer

val ValueWriter = writer<Value> {
    requiredProperty(name = "amount", from = Value::amount, writer = DecimalWriter)
    requiredProperty(name = "currency", from = Value::currency, writer = stringWriter)
}
