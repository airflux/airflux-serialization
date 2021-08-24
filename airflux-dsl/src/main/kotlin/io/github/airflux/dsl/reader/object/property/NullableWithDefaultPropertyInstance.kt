package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.filter.JsPredicate
import io.github.airflux.reader.filter.extension.filter
import io.github.airflux.reader.readNullable
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

internal class NullableWithDefaultPropertyInstance<T : Any> private constructor(
    override val propertyPath: JsPath.Identifiable,
    private var reader: JsReader<T?>
) : NullableWithDefaultProperty<T> {

    companion object {

        fun <T : Any> of(
            propertyPath: JsPath.Identifiable,
            reader: JsReader<T>,
            default: () -> T,
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder
        ): NullableWithDefaultPropertyInstance<T> =
            NullableWithDefaultPropertyInstance(propertyPath) { context, path, input ->
                val lookup = input.lookup(path, propertyPath)
                readNullable(context, lookup, reader, default, invalidTypeErrorBuilder)
            }
    }

    override fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T?> =
        reader.read(context, path, input)

    override fun validation(validator: JsPropertyValidator<T?>): NullableWithDefaultPropertyInstance<T> {
        val previousReader = this.reader
        reader = JsReader { context, path, input ->
            previousReader.read(context, path, input).validation(context, validator)
        }
        return this
    }

    override fun filter(predicate: JsPredicate<T>): NullableWithDefaultPropertyInstance<T> {
        val previousReader = this.reader
        reader = JsReader { context, path, input ->
            previousReader.read(context, path, input).filter(context, predicate)
        }
        return this
    }
}
