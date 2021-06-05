package io.github.airflux.sample.dto.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.readAsList

object CollectionReader {
    fun <T : Any> list(using: JsReader<T>): JsReader<List<T>> = JsReader { context, input ->
        readAsList(input, using, context, ErrorBuilder.InvalidType)
    }
}
