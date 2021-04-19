package io.github.airflux.dsl.reader.`object`

import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.reader.JsReader
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

sealed class Attribute<T : Any, E: JsError> {

    abstract val name: Name
    protected var validator: JsValidator<T, E>? = null
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
                    throw IllegalArgumentException("The path of an attribute is empty.")
        }
    }

    class Required<T : Any, E: JsError>(
        override val name: Name,
        private val reader: JsReader<T, E>,
        private val pathMissingErrorBuilder: () -> E,
        private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
    ) : Attribute<T, E>() {

        fun read(input: JsValue): JsResult<T, E> = validation(encode(input), validator)

        fun validation(validator: JsValidator<T, E>): Required<T, E> = apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T, E> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readRequired(input, simpleName, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
            else
                readRequired(input, name.value, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T, E>, validator: JsValidator<T, E>?): JsResult<T, E> =
            if (validator == null) result else result.validation(validator)
    }

    class Defaultable<T : Any, E: JsError>(
        override val name: Name,
        private val reader: JsReader<T, E>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
    ) : Attribute<T, E>() {

        fun read(input: JsValue): JsResult<T, E> = validation(encode(input), validator)

        fun validation(validator: JsValidator<T, E>): Defaultable<T, E> =
            apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T, E> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readWithDefault(input, simpleName, reader, default, invalidTypeErrorBuilder)
            else
                readWithDefault(input, name.value, reader, default, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T, E>, validator: JsValidator<T, E>?): JsResult<T, E> =
            if (validator == null) result else result.validation(validator)
    }

    class Optional<T : Any, E: JsError>(
        override val name: Name,
        private val reader: JsReader<T, E>,
        private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
    ) : Attribute<T, E>() {

        fun read(input: JsValue): JsResult<T?, E> = validation(encode(input), validator)

        fun validation(validator: JsValidator<T, E>): Optional<T, E> = apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T?, E> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readOptional(input, simpleName, reader, invalidTypeErrorBuilder)
            else
                readOptional(input, name.value, reader, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T?, E>, validator: JsValidator<T, E>?): JsResult<T?, E> =
            if (validator == null) result else result.validation(applyIfPresent(validator))
    }

    class OptionalWithDefault<T : Any, E: JsError>(
        override val name: Name,
        private val reader: JsReader<T, E>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
    ) : Attribute<T, E>() {

        fun read(input: JsValue): JsResult<T, E> = validation(encode(input), validator)

        fun validation(validator: JsValidator<T, E>): OptionalWithDefault<T, E> =
            apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T, E> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readOptional(input, simpleName, reader, default, invalidTypeErrorBuilder)
            else
                readOptional(input, name.value, reader, default, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T, E>, validator: JsValidator<T, E>?): JsResult<T, E> =
            if (validator == null) result else result.validation(validator)
    }

    class Nullable<T : Any, E: JsError>(
        override val name: Name,
        private val reader: JsReader<T, E>,
        private val pathMissingErrorBuilder: () -> E,
        private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
    ) : Attribute<T, E>() {

        fun read(input: JsValue): JsResult<T?, E> = validation(encode(input), validator)

        fun validation(validator: JsValidator<T, E>): Nullable<T, E> = apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T?, E> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readNullable(input, simpleName, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
            else
                readNullable(input, name.value, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T?, E>, validator: JsValidator<T, E>?): JsResult<T?, E> =
            if (validator == null) result else result.validation(applyIfPresent(validator))
    }

    class NullableWithDefault<T : Any, E: JsError>(
        override val name: Name,
        private val reader: JsReader<T, E>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: (expected: JsValue.Type, actual: JsValue.Type) -> E
    ) : Attribute<T, E>() {

        fun read(input: JsValue): JsResult<T?, E> = validation(encode(input), validator)

        fun validation(validator: JsValidator<T, E>): NullableWithDefault<T, E> =
            apply { this.validator = validator }

        private fun encode(input: JsValue): JsResult<T?, E> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readNullable(input, simpleName, reader, default, invalidTypeErrorBuilder)
            else
                readNullable(input, name.value, reader, default, invalidTypeErrorBuilder)
        }

        private fun validation(result: JsResult<T?, E>, validator: JsValidator<T, E>?): JsResult<T?, E> =
            if (validator == null) result else result.validation(applyIfPresent(validator))
    }
}
