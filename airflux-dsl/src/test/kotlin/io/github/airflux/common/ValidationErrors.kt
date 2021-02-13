package io.github.airflux.common

import io.github.airflux.reader.result.JsError

sealed class ValidationErrors : JsError.Validation() {
    object Strings {
        object IsEmpty : ValidationErrors()
    }
}
