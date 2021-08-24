package io.github.airflux.reader.validator.base

import io.github.airflux.reader.validator.JsPropertyValidator

fun <T> applyIfPresent(validator: JsPropertyValidator<T>) = applyIfNotNull(validator)

fun <T> applyIfNotNull(validator: JsPropertyValidator<T>) =
    JsPropertyValidator<T?> { context, path, value ->
        if (value != null) validator.validation(context, path, value) else emptyList()
    }
