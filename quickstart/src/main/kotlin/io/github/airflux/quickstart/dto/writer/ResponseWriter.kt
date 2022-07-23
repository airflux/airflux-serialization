package io.github.airflux.quickstart.dto.writer

import io.github.airflux.dsl.writer.`object`.builder.property.specification.nonNullable
import io.github.airflux.dsl.writer.`object`.builder.writer
import io.github.airflux.quickstart.dto.Response

val ResponseWriter = writer<Response> {
    property(nonNullable(name = "tender", from = Response::tender, writer = TenderWriter))
}
