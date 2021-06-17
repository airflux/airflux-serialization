package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator

fun <T, E : JsError> applyIfPresent(validator: JsValidator<T, E>) = applyIfNotNull(validator)

fun <T, E> applyIfNotNull(validator: JsValidator<T, E>)
    where E : JsError =
    JsValidator<T?, E> { context, path, value ->
        if (value != null)
            validator.validation(context, path, value)
        else
            JsValidationResult.Success
    }
