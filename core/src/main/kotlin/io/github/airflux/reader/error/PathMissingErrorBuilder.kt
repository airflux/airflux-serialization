package io.github.airflux.reader.error

import io.github.airflux.reader.result.JsError

fun interface PathMissingErrorBuilder {
    fun build(): JsError
}
