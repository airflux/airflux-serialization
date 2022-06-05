package io.github.airflux.dsl.reader.array.item.specification

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.validator.JsValidator

public sealed interface JsArrayItemSpec<out T> {
    public val reader: JsReader<T>

    public sealed interface NonNullable<out T> : JsArrayItemSpec<T> {
        public infix fun validation(validator: JsValidator<T>): NonNullable<T>
    }

    public sealed interface Nullable<out T> : JsArrayItemSpec<T?> {
        public infix fun validation(validator: JsValidator<T?>): Nullable<T>
    }
}
