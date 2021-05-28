package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.value.JsValue

sealed interface JsReaderProperty<T : Any> {

    val name: Name

    class Name private constructor(val value: JsPath) {

        val simpleName: String?
            get() = if (value.elements.size == 1 && value.elements[0] is KeyPathElement)
                (value.elements[0] as KeyPathElement).key
            else
                null

        companion object {

            fun of(path: JsPath): Name =
                if (path.elements.isNotEmpty())
                    Name(path)
                else
                    throw IllegalArgumentException("The path of an property is empty.")
        }
    }

    sealed interface Required<T : Any> : JsReaderProperty<T> {

        fun read(input: JsValue, context: JsReaderContext?): JsResult<T>

        fun <E : JsError> validation(validator: JsValidator<T, E>): Required<T>
    }

    sealed interface Defaultable<T : Any> : JsReaderProperty<T> {

        fun read(input: JsValue, context: JsReaderContext?): JsResult<T>

        fun <E : JsError> validation(validator: JsValidator<T, E>): Defaultable<T>
    }

    sealed interface Optional<T : Any> : JsReaderProperty<T> {

        fun read(input: JsValue, context: JsReaderContext?): JsResult<T?>

        fun <E : JsError> validation(validator: JsValidator<T, E>): Optional<T>
    }

    sealed interface OptionalWithDefault<T : Any> : JsReaderProperty<T> {

        fun read(input: JsValue, context: JsReaderContext?): JsResult<T>

        fun <E : JsError> validation(validator: JsValidator<T, E>): OptionalWithDefault<T>
    }

    sealed interface Nullable<T : Any> : JsReaderProperty<T> {

        fun read(input: JsValue, context: JsReaderContext?): JsResult<T?>

        fun <E : JsError> validation(validator: JsValidator<T, E>): Nullable<T>
    }

    sealed interface NullableWithDefault<T : Any> : JsReaderProperty<T> {

        fun read(input: JsValue, context: JsReaderContext?): JsResult<T?>

        fun <E : JsError> validation(validator: JsValidator<T, E>): NullableWithDefault<T>
    }
}
