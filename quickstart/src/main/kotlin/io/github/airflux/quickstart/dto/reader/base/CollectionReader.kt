package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.array.readArray
import io.github.airflux.core.value.extension.readAsArray

object CollectionReader {
    fun <T : Any> list(using: JsReader<T>): JsReader<List<T>> = JsReader { context, location, input ->
        input.readAsArray(context, location) { c, l, v ->
            readArray(c, l, v, using)
        }
    }
}
