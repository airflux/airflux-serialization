package io.github.airflux.core.reader.validator.base

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.validator.JsPropertyValidator

fun <T> JsPropertyValidator<T>.applyIfNotNull() =
    JsPropertyValidator<T?> { context, location, value ->
        if (value != null) validation(context, location, value) else null
    }

fun <T> JsPropertyValidator<T>.applyIf(predicate: (JsReaderContext, JsLocation, T) -> Boolean) =
    JsPropertyValidator<T> { context, location, value ->
        if (predicate(context, location, value)) validation(context, location, value) else null
    }
