package io.github.airflux.reader.validator

import io.github.airflux.reader.result.JsError

sealed class JsValidationResult<out E : JsError.Validation.Reason> {

    object Success : JsValidationResult<Nothing>()
    data class Failure<E : JsError.Validation.Reason>(val reason: E) : JsValidationResult<E>()
}
