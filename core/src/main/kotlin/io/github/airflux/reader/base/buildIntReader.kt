package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.ValueCastErrorBuilder
import io.github.airflux.reader.result.asFailure
import io.github.airflux.reader.result.asSuccess
import io.github.airflux.value.extension.readAsNumber

/**
 * Reader for primitive [Int] type.
 */
fun buildIntReader(
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    valueCastErrorBuilder: ValueCastErrorBuilder
): JsReader<Int> =
    JsReader { _, location, input ->
        input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
            try {
                text.toInt().asSuccess(location = p)
            } catch (expected: NumberFormatException) {
                valueCastErrorBuilder.build(text, Int::class).asFailure(location = p)
            }
        }
    }
