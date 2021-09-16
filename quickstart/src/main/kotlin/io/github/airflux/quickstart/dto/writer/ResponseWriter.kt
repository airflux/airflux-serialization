package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.Response
import io.github.airflux.quickstart.dto.writer.base.writer

val ResponseWriter = writer<Response> {
    requiredProperty(name = "tender", from = Response::tender, writer = TenderWriter)
}
