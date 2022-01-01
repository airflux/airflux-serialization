package io.github.airflux.writer.base

import io.github.airflux.value.JsNumber
import io.github.airflux.writer.JsWriter

/**
 * Writer for primitive [Long] type.
 */
fun buildLongWriter(): JsWriter<Long> = JsWriter { value -> JsNumber.valueOf(value) }
