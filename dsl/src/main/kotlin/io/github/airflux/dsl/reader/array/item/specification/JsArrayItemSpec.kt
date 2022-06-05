package io.github.airflux.dsl.reader.array.item.specification

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.validator.JsValidator

sealed interface JsArrayItemSpec<out T> {
    val reader: JsReader<T>

    sealed interface NonNullable<out T> : JsArrayItemSpec<T> {
        infix fun validation(validator: JsValidator<T>): NonNullable<T>
    }

    sealed interface Nullable<out T> : JsArrayItemSpec<T?> {
        infix fun validation(validator: JsValidator<T?>): Nullable<T>
    }
}
