package io.github.airflux.dsl

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsValue

@Suppress("unused")
object ReaderDsl {

    inline fun <T> reader(crossinline block: (JsValue) -> JsResult<T>): JsReader<T> =
        JsReader { input -> block(input) }

    fun <T> read(from: JsValue, using: JsReader<T>): JsResult<T> = using.read(from)
}
