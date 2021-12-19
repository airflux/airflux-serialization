package io.github.airflux.writer.base

import io.github.airflux.value.JsBoolean
import io.github.airflux.writer.JsWriter

/**
 * Writer for primitive [Boolean] type.
 */
fun buildBooleanWriter(): JsWriter<Boolean> =
    JsWriter { value -> if (value) JsBoolean.True else JsBoolean.False }
