package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.core.path.JsPath
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.readNullable
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.extension.filter
import io.github.airflux.core.reader.validator.JsPropertyValidator
import io.github.airflux.core.reader.validator.extension.validation
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.value.extension.lookup

internal class NullableWithDefaultPropertyInstance<T : Any> private constructor(
    override val path: JsPath,
    private var reader: JsReader<T?>
) : NullableWithDefaultProperty<T> {

    companion object {

        fun <T : Any> of(
            path: JsPath,
            reader: JsReader<T>,
            default: () -> T,
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder
        ): NullableWithDefaultProperty<T> =
            NullableWithDefaultPropertyInstance(path) { context, location, input ->
                val lookup = input.lookup(location, path)
                readNullable(context, lookup, reader, default, invalidTypeErrorBuilder)
            }
    }

    override fun read(context: JsReaderContext, location: JsLocation, input: JsValue): JsResult<T?> =
        reader.read(context, location, input)

    override fun validation(validator: JsPropertyValidator<T?>): NullableWithDefaultPropertyInstance<T> {
        val previousReader = this.reader
        reader = JsReader { context, location, input ->
            previousReader.read(context, location, input).validation(context, validator)
        }
        return this
    }

    override fun filter(predicate: JsPredicate<T>): NullableWithDefaultPropertyInstance<T> {
        val previousReader = this.reader
        reader = JsReader { context, location, input ->
            previousReader.read(context, location, input).filter(context, predicate)
        }
        return this
    }
}
