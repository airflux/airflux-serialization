package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.filter.JsPredicate
import io.github.airflux.reader.filter.extension.filter
import io.github.airflux.reader.readOptional
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

internal class OptionalPropertyInstance<T : Any> private constructor(
    override val path: JsPath.Identifiable,
    private var reader: JsReader<T?>
) : OptionalProperty<T> {

    companion object {

        fun <T : Any> of(
            path: JsPath.Identifiable,
            reader: JsReader<T>,
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder
        ): OptionalProperty<T> =
            OptionalPropertyInstance(path) { context, location, input ->
                val lookup = input.lookup(location, path)
                readOptional(context, lookup, reader, invalidTypeErrorBuilder)
            }
    }

    override fun read(context: JsReaderContext, location: JsLocation, input: JsValue): JsResult<T?> =
        reader.read(context, location, input)

    override fun validation(validator: JsPropertyValidator<T?>): OptionalPropertyInstance<T> {
        val previousReader = this.reader
        reader = JsReader { context, location, input ->
            previousReader.read(context, location, input).validation(context, validator)
        }
        return this
    }

    override fun filter(predicate: JsPredicate<T>): OptionalPropertyInstance<T> {
        val previousReader = this.reader
        reader = JsReader { context, location, input ->
            previousReader.read(context, location, input).filter(context, predicate)
        }
        return this
    }
}
