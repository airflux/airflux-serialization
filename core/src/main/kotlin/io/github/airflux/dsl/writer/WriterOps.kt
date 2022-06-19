package io.github.airflux.dsl.writer

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.writer.JsArrayWriter
import io.github.airflux.core.writer.JsObjectWriter
import io.github.airflux.core.writer.JsWriter
import io.github.airflux.core.writer.context.JsWriterContext
import io.github.airflux.dsl.writer.array.builder.JsArrayWriterBuilder
import io.github.airflux.dsl.writer.`object`.builder.JsObjectWriterBuilder

public fun <T : Any> T.serialization(context: JsWriterContext, location: JsLocation, writer: JsWriter<T>): JsValue? =
    writer.write(context, location, this)

public fun <T : Any> writer(block: JsObjectWriterBuilder<T>.() -> Unit): JsObjectWriter<T> =
    JsObjectWriterBuilder<T>().apply(block).build()

public fun <T : Any> arrayWriter(
    block: JsArrayWriterBuilder<T>.() -> JsArrayWriterBuilder.WriterBuilder<T>
): JsArrayWriter<T> =
    JsArrayWriterBuilder<T>().block().build()
