package io.github.airflux.core.reader.error

import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.value.JsValue

fun interface InvalidTypeErrorBuilder {
    fun build(expected: JsValue.Type, actual: JsValue.Type): JsError
}
