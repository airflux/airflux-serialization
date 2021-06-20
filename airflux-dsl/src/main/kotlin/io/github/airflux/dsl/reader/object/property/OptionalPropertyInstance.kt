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
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

internal class OptionalPropertyInstance<T : Any> private constructor(
    override val propertyPath: JsPath.Identifiable,
    private val reader: JsReader<T?>
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

    override fun <E : JsError> validation(validator: JsValidator<T?, E>): OptionalPropertyInstance<T> =
        OptionalPropertyInstance(
            propertyPath = this.propertyPath,
            reader = { context, path, input ->
                this.read(context, path, input).validation(context, validator)
            }
        )

    override fun filter(predicate: JsPredicate<T>): OptionalPropertyInstance<T> = OptionalPropertyInstance(
        propertyPath = this.propertyPath,
        reader = { context, path, input ->
            this.read(context, path, input)
                .filter(context, predicate)
        }
    )
}
