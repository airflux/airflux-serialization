package io.github.airflux.sample.dto.writer

import io.github.airflux.dsl.WriterDsl.objectWriter
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.writer.JsWriter
import io.github.airflux.writer.base.BasePrimitiveWriter
import java.math.BigDecimal

val ValueWriter: JsWriter<Value> = objectWriter {
    writeRequired(from = Value::amount, to = "amount", using = DecimalWriter)
    writeRequired(from = Value::currency, to = "currency", using = BasePrimitiveWriter.string)
}

val value = Value(amount = BigDecimal(125.52), currency = "USD")