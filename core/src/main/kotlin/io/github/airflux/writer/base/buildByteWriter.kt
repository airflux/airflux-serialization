package io.github.airflux.writer.base

import io.github.airflux.value.JsNumber
import io.github.airflux.writer.JsWriter

/**
 * Writer for primitive [Byte] type.
 */
fun buildByteWriter(): JsWriter<Byte> = JsWriter { value -> JsNumber.valueOf(value) }
