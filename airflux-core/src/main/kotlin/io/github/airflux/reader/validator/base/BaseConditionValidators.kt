package io.github.airflux.reader.validator.base

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.validator.JsPropertyValidator

fun <T, E : JsError> applyIfPresent(validator: JsPropertyValidator<T, E>) = applyIfNotNull(validator)

fun <T, E> applyIfNotNull(validator: JsPropertyValidator<T, E>)
    where E : JsError =
    JsPropertyValidator<T?, E> { context, path, value ->
        if (value != null) validator.validation(context, path, value) else null
    }
