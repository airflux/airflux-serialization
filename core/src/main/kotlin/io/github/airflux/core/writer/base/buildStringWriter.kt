package io.github.airflux.core.writer.base

import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.JsWriter

/**
 * Writer for primitive [String] type.
 */
fun buildStringWriter(): JsWriter<String> = JsWriter { value -> JsString(value) }
