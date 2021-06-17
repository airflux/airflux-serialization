package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath
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
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.reader.validator.base.applyIfPresent
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

internal sealed class JsReaderPropertyInstance<T : Any> {

    protected var validator: JsValidator<T, JsError>? = null
        set(value) {
            if (field == null) field = value else throw IllegalStateException("Reassigned validator.")
        }

    internal class Required<T : Any>(
        override val propertyPath: JsPath.Identifiable,
        private val reader: JsReader<T>,
        private val pathMissingErrorBuilder: PathMissingErrorBuilder,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.Required<T> {

        override fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T> =
            validation(context, encode(context, path, input), validator)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): Required<T> =
            apply { this.validator = validator }

        fun encode(context: JsReaderContext?, resultPath: JsResultPath, input: JsValue): JsResult<T> {
            val lookup = input.lookup(resultPath, propertyPath)
            return readRequired(context, lookup, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }

        fun validation(
            context: JsReaderContext?,
            result: JsResult<T>,
            validator: JsValidator<T, JsError>?
        ): JsResult<T> =
            if (validator == null) result else result.validation(context, validator)
    }

    internal class Defaultable<T : Any> internal constructor(
        override val propertyPath: JsPath.Identifiable,
        private val reader: JsReader<T>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.Defaultable<T> {

        override fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T> =
            validation(context, encode(context, path, input), validator)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): Defaultable<T> =
            apply { this.validator = validator }

        fun encode(context: JsReaderContext?, resultPath: JsResultPath, input: JsValue): JsResult<T> {
            val lookup = input.lookup(resultPath, propertyPath)
            return readWithDefault(context, lookup, reader, default, invalidTypeErrorBuilder)
        }

        fun validation(
            context: JsReaderContext?,
            result: JsResult<T>,
            validator: JsValidator<T, JsError>?
        ): JsResult<T> =
            if (validator == null) result else result.validation(context, validator)
    }

    internal class Optional<T : Any> internal constructor(
        override val propertyPath: JsPath.Identifiable,
        private val reader: JsReader<T>,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.Optional<T> {

        override fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T?> =
            validation(context, encode(context, path, input), validator)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): Optional<T> =
            apply { this.validator = validator }

        fun encode(context: JsReaderContext?, resultPath: JsResultPath, input: JsValue): JsResult<T?> {
            val lookup = input.lookup(resultPath, propertyPath)
            return readOptional(context, lookup, reader, invalidTypeErrorBuilder)
        }

        fun validation(
            context: JsReaderContext?,
            result: JsResult<T?>,
            validator: JsValidator<T, JsError>?
        ): JsResult<T?> =
            if (validator == null) result else result.validation(context, applyIfPresent(validator))
    }

    internal class OptionalWithDefault<T : Any> internal constructor(
        override val propertyPath: JsPath.Identifiable,
        private val reader: JsReader<T>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.OptionalWithDefault<T> {

        override fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T> =
            validation(context, encode(context, path, input), validator)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): OptionalWithDefault<T> =
            apply { this.validator = validator }

        fun encode(context: JsReaderContext?, resultPath: JsResultPath, input: JsValue): JsResult<T> {
            val lookup = input.lookup(resultPath, propertyPath)
            return readOptional(context, lookup, reader, default, invalidTypeErrorBuilder)
        }

        fun validation(
            context: JsReaderContext?,
            result: JsResult<T>,
            validator: JsValidator<T, JsError>?
        ): JsResult<T> =
            if (validator == null) result else result.validation(context, validator)
    }

    internal class Nullable<T : Any> internal constructor(
        override val propertyPath: JsPath.Identifiable,
        private val reader: JsReader<T>,
        private val pathMissingErrorBuilder: PathMissingErrorBuilder,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.Nullable<T> {

        override fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T?> =
            validation(context, encode(context, path, input), validator)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): Nullable<T> =
            apply { this.validator = validator }

        fun encode(context: JsReaderContext?, resultPath: JsResultPath, input: JsValue): JsResult<T?> {
            val lookup = input.lookup(resultPath, propertyPath)
            return readNullable(context, lookup, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }

        fun validation(
            context: JsReaderContext?,
            result: JsResult<T?>,
            validator: JsValidator<T, JsError>?
        ): JsResult<T?> =
            if (validator == null) result else result.validation(context, applyIfPresent(validator))
    }

    internal class NullableWithDefault<T : Any>(
        override val propertyPath: JsPath.Identifiable,
        private val reader: JsReader<T>,
        private val default: () -> T,
        private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
    ) : JsReaderPropertyInstance<T>(), JsReaderProperty.NullableWithDefault<T> {

        override fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T?> =
            validation(context, encode(context, path, input), validator)

        override fun <E : JsError> validation(validator: JsValidator<T, E>): NullableWithDefault<T> =
            apply { this.validator = validator }

        fun encode(context: JsReaderContext?, resultPath: JsResultPath, input: JsValue): JsResult<T?> {
            val lookup = input.lookup(resultPath, propertyPath)
            return readNullable(context, lookup, reader, default, invalidTypeErrorBuilder)
        }

        fun validation(
            context: JsReaderContext?,
            result: JsResult<T?>,
            validator: JsValidator<T, JsError>?
        ): JsResult<T?> =
            if (validator == null) result else result.validation(context, applyIfPresent(validator))
    }
}
