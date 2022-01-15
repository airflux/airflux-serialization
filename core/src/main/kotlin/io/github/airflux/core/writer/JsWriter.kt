package io.github.airflux.core.writer

import io.github.airflux.core.value.JsValue

fun interface JsWriter<in T : Any> {

    infix fun write(value: T): JsValue
}

fun interface JsObjectWriter<in T : Any> : JsWriter<T>

fun interface JsArrayWriter<in T : Any> : JsWriter<T>
