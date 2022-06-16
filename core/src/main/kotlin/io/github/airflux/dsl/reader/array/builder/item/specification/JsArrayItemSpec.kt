package io.github.airflux.dsl.reader.array.builder.item.specification

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.validator.JsValidator

public sealed interface JsArrayItemSpec<T> {
    public val reader: JsReader<T>

    public sealed interface NonNullable<T> : JsArrayItemSpec<T> {
        public infix fun validation(validator: JsValidator<T>): NonNullable<T>
        public infix fun or(alt: NonNullable<T>): NonNullable<T>
    }

    public sealed interface Nullable<T> : JsArrayItemSpec<T?> {
        public infix fun validation(validator: JsValidator<T?>): Nullable<T>
        public infix fun or(alt: Nullable<T>): Nullable<T>
    }
}
