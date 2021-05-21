package io.github.airflux.sample.dto.writer

import io.github.airflux.sample.dto.Response
import io.github.airflux.sample.dto.writer.base.writer

val ResponseWriter = writer<Response> {
    requiredProperty(name = "tender", from = Response::tender, writer = TenderWriter)
}
