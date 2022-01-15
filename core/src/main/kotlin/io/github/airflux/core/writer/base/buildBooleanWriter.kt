package io.github.airflux.core.writer.base

import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.writer.JsWriter

/**
 * Writer for primitive [Boolean] type.
 */
fun buildBooleanWriter(): JsWriter<Boolean> =
    JsWriter { value -> if (value) JsBoolean.True else JsBoolean.False }
