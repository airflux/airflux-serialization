package io.github.airflux.reader.filter.extension

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.filter.JsPredicate
import io.github.airflux.reader.result.JsResult

infix fun <T> JsReader<T>.filter(predicate: JsPredicate<T>): JsReader<T?> =
    JsReader { context, location, input ->
        this@filter.read(context, location, input)
            .filter(context, predicate)
    }

fun <T> JsResult<T?>.filter(predicate: JsPredicate<T>): JsResult<T?> = filter(context = JsReaderContext(), predicate = predicate)

fun <T> JsResult<T?>.filter(context: JsReaderContext, predicate: JsPredicate<T>): JsResult<T?> =
    when (this) {
        is JsResult.Success -> if (this.value != null) {
            if (predicate.test(context, this.location, this.value)) this
            else JsResult.Success(value = null, location = this.location)
        } else
            this

        is JsResult.Failure -> this
    }
