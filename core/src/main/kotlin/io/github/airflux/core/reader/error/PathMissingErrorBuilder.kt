package io.github.airflux.core.reader.error

import io.github.airflux.core.reader.result.JsError

fun interface PathMissingErrorBuilder {
    fun build(): JsError
}
