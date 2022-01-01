package io.github.airflux.writer.base

import io.github.airflux.value.JsNumber
import io.github.airflux.writer.JsWriter

/**
 * Writer for primitive [Int] type.
 */
fun buildIntWriter(): JsWriter<Int> = JsWriter { value -> JsNumber.valueOf(value) }
