package io.github.airflux.writer

import io.github.airflux.value.JsValue

fun interface JsWriter<in T : Any> {

    infix fun write(value: T): JsValue
}

fun interface JsObjectWriter<in T : Any> : JsWriter<T>

fun interface JsArrayWriter<in T : Any> : JsWriter<T>
