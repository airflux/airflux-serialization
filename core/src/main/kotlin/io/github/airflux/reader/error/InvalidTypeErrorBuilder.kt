package io.github.airflux.reader.error

import io.github.airflux.reader.result.JsError
import io.github.airflux.value.JsValue

fun interface InvalidTypeErrorBuilder {
    fun build(expected: JsValue.Type, actual: JsValue.Type): JsError
}
