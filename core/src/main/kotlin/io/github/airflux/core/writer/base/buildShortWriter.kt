package io.github.airflux.core.writer.base

import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.writer.JsWriter

/**
 * Writer for primitive [Short] type.
 */
fun buildShortWriter(): JsWriter<Short> = JsWriter { value -> JsNumber.valueOf(value) }
