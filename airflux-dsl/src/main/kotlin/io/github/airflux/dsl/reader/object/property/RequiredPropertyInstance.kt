package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.PathMissingErrorBuilder
import io.github.airflux.reader.readRequired
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup

internal class RequiredPropertyInstance<T : Any> private constructor(
    override val propertyPath: JsPath.Identifiable,
    private var reader: JsReader<T>
) : RequiredProperty<T> {

    companion object {

        fun <T : Any> of(
            propertyPath: JsPath.Identifiable,
            reader: JsReader<T>,
            pathMissingErrorBuilder: PathMissingErrorBuilder,
            invalidTypeErrorBuilder: InvalidTypeErrorBuilder
        ): RequiredPropertyInstance<T> = RequiredPropertyInstance(propertyPath) { context, path, input ->
            val lookup = input.lookup(path, propertyPath)
            readRequired(context, lookup, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
        }
    }

    override fun read(context: JsReaderContext, path: JsResultPath, input: JsValue): JsResult<T> =
        reader.read(context, path, input)

    override fun validation(validator: JsPropertyValidator<T>): RequiredPropertyInstance<T> {
        val previousReader = this.reader
        reader = JsReader { context, path, input ->
            previousReader.read(context, path, input).validation(context, validator)
        }
        return this
    }
}
