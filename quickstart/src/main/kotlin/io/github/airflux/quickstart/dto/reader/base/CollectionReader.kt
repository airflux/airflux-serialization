package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.readAsList

object CollectionReader {
    fun <T : Any> list(using: JsReader<T>): JsReader<List<T>> = JsReader { context, location, input ->
        readAsList(context, location, input, using)
    }
}
