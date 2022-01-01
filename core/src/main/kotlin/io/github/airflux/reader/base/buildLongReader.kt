package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.ValueCastErrorBuilder
import io.github.airflux.reader.result.asFailure
import io.github.airflux.reader.result.asSuccess
import io.github.airflux.value.extension.readAsNumber

/**
 * Reader for primitive [Long] type.
 */
fun buildLongReader(
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    valueCastErrorBuilder: ValueCastErrorBuilder
): JsReader<Long> =
    JsReader { _, location, input ->
        input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
            try {
                text.toLong().asSuccess(location = p)
            } catch (expected: NumberFormatException) {
                valueCastErrorBuilder.build(text, Long::class).asFailure(location = p)
            }
        }
    }
