package io.github.airflux.reader.extension

import io.github.airflux.reader.predicate.JsPredicate
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.extension.filter

infix fun <T> JsReader<T?>.filter(predicate: JsPredicate<T>): JsReader<T?> =
    JsReader { context, location, input ->
        this@filter.read(context, location, input)
            .filter(context, predicate)
    }
