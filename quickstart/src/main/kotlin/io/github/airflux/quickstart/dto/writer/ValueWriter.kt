package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.quickstart.dto.writer.base.PrimitiveWriter.DecimalWriter
import io.github.airflux.quickstart.dto.writer.base.writer
import io.github.airflux.writer.base.BasePrimitiveWriter

val ValueWriter = writer<Value> {
    requiredProperty(name = "amount", from = Value::amount, writer = DecimalWriter)
    requiredProperty(name = "currency", from = Value::currency, writer = BasePrimitiveWriter.string)
}
