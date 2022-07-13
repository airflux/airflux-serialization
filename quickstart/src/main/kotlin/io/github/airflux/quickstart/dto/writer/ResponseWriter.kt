package io.github.airflux.quickstart.dto.writer

import io.github.airflux.dsl.writer.`object`.builder.property.specification.required
import io.github.airflux.dsl.writer.`object`.builder.writer
import io.github.airflux.quickstart.dto.Response

val ResponseWriter = writer<Response> {
    property(required(name = "tender", from = Response::tender, writer = TenderWriter))
}
