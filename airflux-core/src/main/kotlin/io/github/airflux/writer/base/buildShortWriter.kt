package io.github.airflux.writer.base

import io.github.airflux.value.JsNumber
import io.github.airflux.writer.JsWriter

/**
 * Writer for primitive [Short] type.
 */
fun buildShortWriter(): JsWriter<Short> = JsWriter { value -> JsNumber.valueOf(value) }
