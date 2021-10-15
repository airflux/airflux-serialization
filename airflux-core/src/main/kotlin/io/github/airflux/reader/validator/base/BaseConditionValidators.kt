package io.github.airflux.reader.validator.base

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator

fun <T> JsPropertyValidator<T>.applyIfPresent() = applyIfNotNull()

fun <T> JsPropertyValidator<T>.applyIfNotNull() =
    JsPropertyValidator<T?> { context, path, value ->
        if (value != null) validation(context, path, value) else null
    }

fun <T> JsPropertyValidator<T>.applyIf(predicate: (JsReaderContext, JsResultPath, T) -> Boolean) =
    JsPropertyValidator<T> { context, path, value ->
        if (predicate(context, path, value)) validation(context, path, value) else null
    }
