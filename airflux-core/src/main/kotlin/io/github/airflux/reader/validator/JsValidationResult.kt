package io.github.airflux.reader.validator

import io.github.airflux.reader.result.JsError

sealed class JsValidationResult<out E : JsError> {

    object Success : JsValidationResult<Nothing>()

    data class Failure<E : JsError>(val reason: E) : JsValidationResult<E>()
}
