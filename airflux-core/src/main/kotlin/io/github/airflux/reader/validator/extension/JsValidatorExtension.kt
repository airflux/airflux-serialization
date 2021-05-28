package io.github.airflux.reader.validator.extension

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator

infix fun <T, E : JsError> JsReader<T>.validation(validator: JsValidator<T, E>): JsReader<T> =
    JsReader { input, context ->
        this@validation.read(input, context)
            .validation(validator, context)
    }

fun <T, E : JsError> JsResult<T>.validation(validator: JsValidator<T, E>, context: JsReaderContext?): JsResult<T> =
    when (this) {
        is JsResult.Success -> when (val validated = validator.validation(this.value, context)) {
            is JsValidationResult.Success -> this
            is JsValidationResult.Failure -> JsResult.Failure(path = this.path, error = validated.reason)
        }
        is JsResult.Failure -> this
    }
