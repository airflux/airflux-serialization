package io.github.airflux.core.reader.result.extension

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.result.JsResult

fun <T> JsResult<T?>.filter(predicate: JsPredicate<T>): JsResult<T?> =
    filter(context = JsReaderContext(), predicate = predicate)

fun <T> JsResult<T?>.filter(context: JsReaderContext, predicate: JsPredicate<T>): JsResult<T?> =
    when (this) {
        is JsResult.Success -> if (this.value != null) {
            if (predicate.test(context, this.location, this.value)) this
            else JsResult.Success(this.location, null)
        } else
            this

        is JsResult.Failure -> this
    }
