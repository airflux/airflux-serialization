package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.filter.JsPredicate
import io.github.airflux.reader.filter.extension.filter
import io.github.airflux.reader.readOptional
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

internal class OptionalPropertyInstance<T : Any> private constructor(
    override val propertyPath: JsPath.Identifiable,
    private var reader: JsReader<T?>
) : OptionalProperty<T> {

    companion object {

        fun <T : Any> of(
            propertyPath: JsPath.Identifiable,
            reader: JsReader<T>,
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder
        ): OptionalPropertyInstance<T> = OptionalPropertyInstance(propertyPath) { context, path, input ->
            val lookup = input.lookup(path, propertyPath)
            readOptional(context, lookup, reader, invalidTypeErrorBuilder)
        }
    }

    override fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T?> =
        reader.read(context, path, input)

    override fun <E : JsError> validation(validator: JsPropertyValidator<T?, E>): OptionalPropertyInstance<T> {
        val previousReader = this.reader
        reader = JsReader { context, path, input ->
            previousReader.read(context, path, input).validation(context, validator)
        }
        return this
    }

    override fun filter(predicate: JsPredicate<T>): OptionalPropertyInstance<T> {
        val previousReader = this.reader
        reader = JsReader { context, path, input ->
            previousReader.read(context, path, input).filter(context, predicate)
        }
        return this
    }
}
