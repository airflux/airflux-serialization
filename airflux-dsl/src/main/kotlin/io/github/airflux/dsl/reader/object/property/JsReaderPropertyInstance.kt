package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
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

internal sealed class JsReaderPropertyInstance<T : Any> {

    protected var validator: JsValidator<T, JsError>? = null
        set(value) {
            if (field == null) field = value else throw IllegalStateException("Reassigned validator.")
        }

    internal class Required<T : Any>(
        override val name: JsReaderProperty.Name,
        private val reader: JsReader<T>,
        private val pathMissingErrorBuilder: PathMissingErrorBuilder,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.Required<T> {

        override fun read(input: JsValue, context: JsReaderContext?): JsResult<T> =
            validation(encode(input, context), validator, context)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): Required<T> =
            apply { this.validator = validator }

        fun encode(input: JsValue, context: JsReaderContext?): JsResult<T> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readRequired(input, simpleName, reader, context, pathMissingErrorBuilder, invalidTypeErrorBuilder)
            else
                readRequired(input, name.value, reader, context, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }

        fun validation(
            result: JsResult<T>,
            validator: JsValidator<T, JsError>?,
            context: JsReaderContext?
        ): JsResult<T> =
            if (validator == null) result else result.validation(context, validator)
    }

    internal class Defaultable<T : Any> internal constructor(
        override val name: JsReaderProperty.Name,
        private val reader: JsReader<T>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.Defaultable<T> {

        override fun read(input: JsValue, context: JsReaderContext?): JsResult<T> =
            validation(encode(input, context), validator, context)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): Defaultable<T> =
            apply { this.validator = validator }

        fun encode(input: JsValue, context: JsReaderContext?): JsResult<T> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readWithDefault(input, simpleName, reader, default, context, invalidTypeErrorBuilder)
            else
                readWithDefault(input, name.value, reader, default, context, invalidTypeErrorBuilder)
        }

        fun validation(
            result: JsResult<T>,
            validator: JsValidator<T, JsError>?,
            context: JsReaderContext?
        ): JsResult<T> =
            if (validator == null) result else result.validation(context, validator)
    }

    internal class Optional<T : Any> internal constructor(
        override val name: JsReaderProperty.Name,
        private val reader: JsReader<T>,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.Optional<T> {

        override fun read(input: JsValue, context: JsReaderContext?): JsResult<T?> =
            validation(encode(input, context), validator, context)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): Optional<T> =
            apply { this.validator = validator }

        fun encode(input: JsValue, context: JsReaderContext?): JsResult<T?> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readOptional(input, simpleName, reader, context, invalidTypeErrorBuilder)
            else
                readOptional(input, name.value, reader, context, invalidTypeErrorBuilder)
        }

        fun validation(
            result: JsResult<T?>,
            validator: JsValidator<T, JsError>?,
            context: JsReaderContext?
        ): JsResult<T?> =
            if (validator == null) result else result.validation(context, applyIfPresent(validator))
    }

    internal class OptionalWithDefault<T : Any> internal constructor(
        override val name: JsReaderProperty.Name,
        private val reader: JsReader<T>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.OptionalWithDefault<T> {

        override fun read(input: JsValue, context: JsReaderContext?): JsResult<T> =
            validation(encode(input, context), validator, context)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): OptionalWithDefault<T> =
            apply { this.validator = validator }

        fun encode(input: JsValue, context: JsReaderContext?): JsResult<T> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readOptional(input, simpleName, reader, default, context, invalidTypeErrorBuilder)
            else
                readOptional(input, name.value, reader, default, context, invalidTypeErrorBuilder)
        }

        fun validation(
            result: JsResult<T>,
            validator: JsValidator<T, JsError>?,
            context: JsReaderContext?
        ): JsResult<T> =
            if (validator == null) result else result.validation(context, validator)
    }

    internal class Nullable<T : Any> internal constructor(
        override val name: JsReaderProperty.Name,
        private val reader: JsReader<T>,
        private val pathMissingErrorBuilder: PathMissingErrorBuilder,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.Nullable<T> {

        override fun read(input: JsValue, context: JsReaderContext?): JsResult<T?> =
            validation(encode(input, context), validator, context)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): Nullable<T> =
            apply { this.validator = validator }

        fun encode(input: JsValue, context: JsReaderContext?): JsResult<T?> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readNullable(input, simpleName, reader, context, pathMissingErrorBuilder, invalidTypeErrorBuilder)
            else
                readNullable(input, name.value, reader, context, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }

        fun validation(
            result: JsResult<T?>,
            validator: JsValidator<T, JsError>?,
            context: JsReaderContext?
        ): JsResult<T?> =
            if (validator == null) result else result.validation(context, applyIfPresent(validator))
    }

    internal class NullableWithDefault<T : Any>(
        override val name: JsReaderProperty.Name,
        private val reader: JsReader<T>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.NullableWithDefault<T> {

        override fun read(input: JsValue, context: JsReaderContext?): JsResult<T?> =
            validation(encode(input, context), validator, context)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): NullableWithDefault<T> =
            apply { this.validator = validator }

        fun encode(input: JsValue, context: JsReaderContext?): JsResult<T?> {
            val simpleName = name.simpleName
            return if (simpleName != null)
                readNullable(input, simpleName, reader, default, context, invalidTypeErrorBuilder)
            else
                readNullable(input, name.value, reader, default, context, invalidTypeErrorBuilder)
        }

        fun validation(
            result: JsResult<T?>,
            validator: JsValidator<T, JsError>?,
            context: JsReaderContext?
        ): JsResult<T?> =
            if (validator == null) result else result.validation(context, applyIfPresent(validator))
    }
}
