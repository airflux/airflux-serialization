package io.github.airflux.reader.validator.extension

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.JsPropertyValidator

infix fun <T, E : JsError> JsReader<T>.validation(validator: JsPropertyValidator<T, E>): JsReader<T> =
    JsReader { context, path, input ->
        this@validation.read(context, path, input)
            .validation(context, validator)
    }

fun <T, E : JsError> JsResult<T>.validation(validator: JsPropertyValidator<T, E>): JsResult<T> =
    validation(context = null, validator = validator)

fun <T, E : JsError> JsResult<T>.validation(
    context: JsReaderContext?,
    validator: JsPropertyValidator<T, E>
): JsResult<T> =
    when (this) {
        is JsResult.Success -> {
            val errors = validator.validation(context, this.path, this.value)
            if (errors.isNotEmpty()) JsResult.Failure(path = this.path, errors = errors) else this
        }
        is JsResult.Failure -> this
    }
