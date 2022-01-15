package io.github.airflux.core.writer.base

import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.writer.JsWriter

/**
 * Writer for primitive [Int] type.
 */
fun buildIntWriter(): JsWriter<Int> = JsWriter { value -> JsNumber.valueOf(value) }
