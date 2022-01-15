package io.github.airflux.core.writer.base

import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.writer.JsWriter

/**
 * Writer for primitive [Long] type.
 */
fun buildLongWriter(): JsWriter<Long> = JsWriter { value -> JsNumber.valueOf(value) }
