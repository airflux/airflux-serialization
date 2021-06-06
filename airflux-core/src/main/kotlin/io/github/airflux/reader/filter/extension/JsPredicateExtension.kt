package io.github.airflux.reader.filter.extension

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.filter.JsPredicate
import io.github.airflux.reader.result.JsResult

infix fun <T> JsReader<T>.filter(predicate: JsPredicate<T>): JsReader<T?> =
    JsReader { context, input ->
        this@filter.read(context, input)
            .filter(context, predicate)
    }

fun <T> JsResult<T>.filter(predicate: JsPredicate<T>): JsResult<T?> = filter(context = null, predicate = predicate)

fun <T> JsResult<T>.filter(context: JsReaderContext?, predicate: JsPredicate<T>): JsResult<T?> =
    when (this) {
        is JsResult.Success ->
            if (predicate.test(context, this.value)) this else JsResult.Success(value = null, path = this.path)
        is JsResult.Failure -> this
    }
