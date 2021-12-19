package io.github.airflux.writer.base

import io.github.airflux.value.JsString
import io.github.airflux.writer.JsWriter

/**
 * Writer for primitive [String] type.
 */
fun buildStringWriter(): JsWriter<String> = JsWriter { value -> JsString(value) }
