package io.github.airflux.core.reader.base

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.core.reader.result.asFailure
import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.core.value.extension.readAsNumber

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
