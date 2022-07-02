package io.github.airflux.dsl.reader.array.builder.item.specification

import io.github.airflux.core.reader.JsReader

public sealed class JsArrayItemSpec<out T> {
    public abstract val reader: JsReader<T>

    public class NonNullable<out T> internal constructor(override val reader: JsReader<T>) : JsArrayItemSpec<T>()
    public class Nullable<out T> internal constructor(override val reader: JsReader<T?>) : JsArrayItemSpec<T?>()
}
