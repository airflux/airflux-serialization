package io.github.airflux.core.writer.base

import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.writer.JsWriter

/**
 * Writer for primitive [Byte] type.
 */
fun buildByteWriter(): JsWriter<Byte> = JsWriter { value -> JsNumber.valueOf(value) }
