package io.github.airflux.sample.dto.writer

import io.github.airflux.dsl.WriterDsl.objectWriter
import io.github.airflux.sample.dto.Response
import io.github.airflux.writer.JsWriter

val ResponseWriter: JsWriter<Response> = objectWriter {
    writeRequired(from = Response::value, to = "value", using = ValueWriter)
}
