package io.github.airflux.sample.dto.writer

import io.github.airflux.sample.dto.Response
import io.github.airflux.sample.dto.writer.base.writer
import io.github.airflux.writer.JsWriter

val ResponseWriter: JsWriter<Response> = writer {
    requiredProperty(name = "tender", from = Response::tender, writer = TenderWriter)
}
