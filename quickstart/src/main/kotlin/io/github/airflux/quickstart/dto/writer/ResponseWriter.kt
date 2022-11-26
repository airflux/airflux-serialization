package io.github.airflux.quickstart.dto.writer

import io.github.airflux.quickstart.dto.Response
import io.github.airflux.quickstart.dto.writer.env.WriterCtx
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.dsl.writer.struct.builder.property.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.struct.builder.structWriter

val ResponseWriter: Writer<WriterCtx, Response> = structWriter {
    property(nonNullable(name = "tender", from = Response::tender, writer = TenderWriter))
}
