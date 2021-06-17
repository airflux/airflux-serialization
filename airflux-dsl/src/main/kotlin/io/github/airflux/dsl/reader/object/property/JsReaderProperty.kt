package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.value.JsValue

sealed interface JsReaderProperty<T : Any> {

    val propertyPath: JsPath.Identifiable

    sealed interface Required<T : Any> : JsReaderProperty<T> {

        fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T>

        fun <E : JsError> validation(validator: JsValidator<T, E>): Required<T>
    }

    sealed interface Defaultable<T : Any> : JsReaderProperty<T> {

        fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T>

        fun <E : JsError> validation(validator: JsValidator<T, E>): Defaultable<T>
    }

    sealed interface Optional<T : Any> : JsReaderProperty<T> {

        fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T?>

        fun <E : JsError> validation(validator: JsValidator<T, E>): Optional<T>
    }

    sealed interface OptionalWithDefault<T : Any> : JsReaderProperty<T> {

        fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T>

        fun <E : JsError> validation(validator: JsValidator<T, E>): OptionalWithDefault<T>
    }

    sealed interface Nullable<T : Any> : JsReaderProperty<T> {

        fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T?>

        fun <E : JsError> validation(validator: JsValidator<T, E>): Nullable<T>
    }

    sealed interface NullableWithDefault<T : Any> : JsReaderProperty<T> {

        fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T?>

        fun <E : JsError> validation(validator: JsValidator<T, E>): NullableWithDefault<T>
    }
}
