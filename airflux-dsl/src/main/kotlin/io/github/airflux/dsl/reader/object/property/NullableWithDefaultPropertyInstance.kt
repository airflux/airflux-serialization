package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.filter.JsPredicate
import io.github.airflux.reader.filter.extension.filter
import io.github.airflux.reader.readNullable
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

internal class NullableWithDefaultPropertyInstance<T : Any> private constructor(
    override val propertyPath: JsPath.Identifiable,
    private val reader: JsReader<T?>
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

    override fun <E : JsError> validation(validator: JsValidator<T?, E>): NullableWithDefaultPropertyInstance<T> =
        NullableWithDefaultPropertyInstance(
            propertyPath = this.propertyPath,
            reader = { context, path, input ->
                this.read(context, path, input).validation(context, validator)
            }
        )

    override fun filter(predicate: JsPredicate<T>): NullableWithDefaultPropertyInstance<T> =
        NullableWithDefaultPropertyInstance(
            propertyPath = this.propertyPath,
            reader = { context, path, input ->
                this.read(context, path, input)
                    .filter(context, predicate)
            }
        )
}
