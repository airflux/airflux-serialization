package io.github.airflux.reader.result.extension

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.predicate.JsPredicate
import io.github.airflux.reader.result.JsResult

fun <T> JsResult<T?>.filter(predicate: JsPredicate<T>): JsResult<T?> =
    filter(context = JsReaderContext(), predicate = predicate)

fun <T> JsResult<T?>.filter(context: JsReaderContext, predicate: JsPredicate<T>): JsResult<T?> =
    when (this) {
        is JsResult.Success -> if (this.value != null) {
            if (predicate.test(context, this.location, this.value)) this
            else JsResult.Success(value = null, location = this.location)
        } else
            this

        is JsResult.Failure -> this
    }
