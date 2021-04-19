package io.github.airflux.dsl

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsValue

@Suppress("unused")
object ReaderDsl {

    inline fun <T, E : JsError> reader(crossinline block: (JsValue) -> JsResult<T, E>): JsReader<T, E> =
        JsReader { input -> block(input) }

    fun <T, E : JsError> read(from: JsValue, using: JsReader<T, E>): JsResult<T, E> = using.read(from)
}
