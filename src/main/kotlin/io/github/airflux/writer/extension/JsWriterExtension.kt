package io.github.airflux.writer.extension

import io.github.airflux.value.JsArray
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsValue
import io.github.airflux.writer.JsWriter
import kotlin.reflect.KProperty1

fun <T, R : Any> writeRequiredAttribute(from: KProperty1<T, R>, using: JsWriter<R>): (T) -> JsValue = { target: T ->
    using.write(from.invoke(target))
}

fun <T, R : Any> writeOptionalAttribute(from: KProperty1<T, R?>, using: JsWriter<R>): (T) -> JsValue? = { target: T ->
    from.invoke(target)
        ?.let { value -> using.write(value) }
}

fun <T, R : Any> writeNullableAttribute(from: KProperty1<T, R?>, using: JsWriter<R>): (T) -> JsValue = { target: T ->
    val value = from.invoke(target)
    if (value != null) using.write(value) else JsNull
}

fun <T, R : Any, C : Collection<R>> writeTraversableAttribute(
    from: KProperty1<T, C>,
    using: JsWriter<R>
): (T) -> JsValue = { target: T ->
    from.invoke(target)
        .map { using.write(it) }
        .let { JsArray(it) }
}

fun <T, R : Any, C : Collection<R>> writeOptionalTraversableAttribute(
    from: KProperty1<T, C>,
    using: JsWriter<R>
): (T) -> JsValue? = { target: T ->
    from.invoke(target)
        .map { using.write(it) }
        .takeIf { it.isNotEmpty() }
        ?.let { JsArray(it) }
}
