package io.github.airflux.core.reader.base

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.core.reader.result.asFailure
import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.core.value.extension.readAsNumber

/**
 * Reader for primitive [Short] type.
 */
fun buildShortReader(
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    valueCastErrorBuilder: ValueCastErrorBuilder
): JsReader<Short> =
    JsReader { _, location, input ->
        input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
            try {
                text.toShort().asSuccess(location = p)
            } catch (expected: NumberFormatException) {
                valueCastErrorBuilder.build(text, Short::class).asFailure(location = p)
            }
        }
    }
