package io.github.airflux.reader.validator.extension

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.JsPropertyValidator

infix fun <T> JsReader<T>.validation(validator: JsPropertyValidator<T>): JsReader<T> =
    JsReader { context, location, input ->
        this@validation.read(context, location, input)
            .validation(context, validator)
    }

fun <T> JsResult<T>.validation(validator: JsPropertyValidator<T>): JsResult<T> =
    validation(context = JsReaderContext(), validator = validator)

fun <T> JsResult<T>.validation(context: JsReaderContext, validator: JsPropertyValidator<T>): JsResult<T> =
    when (this) {
        is JsResult.Success -> {
            val errors = validator.validation(context, this.location, this.value)
            if (errors != null) JsResult.Failure(location = this.location, errors = errors) else this
        }
        is JsResult.Failure -> this
    }
