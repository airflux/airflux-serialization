package io.github.airflux.dsl.writer.array.builder.item.specification

import io.github.airflux.core.writer.JsWriter

public sealed interface JsArrayItemSpec<T> {
    public val writer: JsWriter<T>

    public sealed interface NonNullable<T> : JsArrayItemSpec<T>
    public sealed interface Nullable<T> : JsArrayItemSpec<T?>
}
