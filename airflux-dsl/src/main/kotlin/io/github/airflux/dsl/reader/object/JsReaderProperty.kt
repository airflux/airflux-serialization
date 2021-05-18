package io.github.airflux.dsl.reader.`object`

import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.PathMissingErrorBuilder
import io.github.airflux.reader.readNullable
import io.github.airflux.reader.readOptional
import io.github.airflux.reader.readRequired
import io.github.airflux.reader.readWithDefault
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.reader.validator.base.applyIfPresent
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.value.JsValue

sealed class JsReaderProperty<T : Any> {

    abstract val name: Name
    protected var validator: JsValidator<T, JsError>? = null
        set(value) {
            if (field == null) field = value else throw IllegalStateException("Reassigned validator.")
        }

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

    class Required<T : Any>(
        override val name: Name,
        private val reader: JsReader<T>,
        private val pathMissingErrorBuilder: PathMissingErrorBuilder,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderProperty<T>() {

        fun read(input: JsValue): JsResult<T> = validation(encode(input), validator)

        fun <E : JsError> validation(validator: JsValidator<T, E>): Required<T> = apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readRequired(input, simpleName, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
            else
                readRequired(input, name.value, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T>, validator: JsValidator<T, JsError>?): JsResult<T> =
            if (validator == null) result else result.validation(validator)
    }

    class Defaultable<T : Any>(
        override val name: Name,
        private val reader: JsReader<T>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderProperty<T>() {

        fun read(input: JsValue): JsResult<T> = validation(encode(input), validator)

        fun <E : JsError> validation(validator: JsValidator<T, E>): Defaultable<T> =
            apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readWithDefault(input, simpleName, reader, default, invalidTypeErrorBuilder)
            else
                readWithDefault(input, name.value, reader, default, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T>, validator: JsValidator<T, JsError>?): JsResult<T> =
            if (validator == null) result else result.validation(validator)
    }

    class Optional<T : Any>(
        override val name: Name,
        private val reader: JsReader<T>,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderProperty<T>() {

        fun read(input: JsValue): JsResult<T?> = validation(encode(input), validator)

        fun <E : JsError> validation(validator: JsValidator<T, E>): Optional<T> = apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T?> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readOptional(input, simpleName, reader, invalidTypeErrorBuilder)
            else
                readOptional(input, name.value, reader, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T?>, validator: JsValidator<T, JsError>?): JsResult<T?> =
            if (validator == null) result else result.validation(applyIfPresent(validator))
    }

    class OptionalWithDefault<T : Any>(
        override val name: Name,
        private val reader: JsReader<T>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderProperty<T>() {

        fun read(input: JsValue): JsResult<T> = validation(encode(input), validator)

        fun <E : JsError> validation(validator: JsValidator<T, E>): OptionalWithDefault<T> =
            apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readOptional(input, simpleName, reader, default, invalidTypeErrorBuilder)
            else
                readOptional(input, name.value, reader, default, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T>, validator: JsValidator<T, JsError>?): JsResult<T> =
            if (validator == null) result else result.validation(validator)
    }

    class Nullable<T : Any>(
        override val name: Name,
        private val reader: JsReader<T>,
        private val pathMissingErrorBuilder: PathMissingErrorBuilder,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderProperty<T>() {

        fun read(input: JsValue): JsResult<T?> = validation(encode(input), validator)

        fun <E : JsError> validation(validator: JsValidator<T, E>): Nullable<T> = apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T?> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readNullable(input, simpleName, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
            else
                readNullable(input, name.value, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T?>, validator: JsValidator<T, JsError>?): JsResult<T?> =
            if (validator == null) result else result.validation(applyIfPresent(validator))
    }

    class NullableWithDefault<T : Any>(
        override val name: Name,
        private val reader: JsReader<T>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderProperty<T>() {

        fun read(input: JsValue): JsResult<T?> = validation(encode(input), validator)

        fun <E : JsError> validation(validator: JsValidator<T, E>): NullableWithDefault<T> =
            apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T?> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readNullable(input, simpleName, reader, default, invalidTypeErrorBuilder)
            else
                readNullable(input, name.value, reader, default, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T?>, validator: JsValidator<T, JsError>?): JsResult<T?> =
            if (validator == null) result else result.validation(applyIfPresent(validator))
    }
}
