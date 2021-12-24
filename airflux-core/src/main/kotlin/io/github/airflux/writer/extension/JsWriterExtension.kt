package io.github.airflux.writer.extension

import io.github.airflux.value.JsArray
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsValue
import io.github.airflux.writer.JsArrayWriter
import io.github.airflux.writer.JsWriter

fun <T, P : Any> writeAsRequired(receiver: T, getter: (T) -> P, using: JsWriter<P>): JsValue =
    using.write(getter.invoke(receiver))

fun <T, P : Any> writeAsOptional(receiver: T, getter: (T) -> P?, using: JsWriter<P>): JsValue? =
    getter.invoke(receiver)
        ?.let { value -> using.write(value) }

fun <T, P : Any> writeAsNullable(receiver: T, getter: (T) -> P?, using: JsWriter<P>): JsValue {
    val value = getter.invoke(receiver)
    return if (value != null) using.write(value) else JsNull
}

fun <T : Any> arrayWriter(using: JsWriter<T>): JsArrayWriter<Collection<T>> = JsArrayWriter { value ->
    JsArray(items = value.map { item -> using.write(item) })
}
