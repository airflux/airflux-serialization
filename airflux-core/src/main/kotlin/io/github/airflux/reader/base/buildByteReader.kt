package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.ValueCastErrorBuilder
import io.github.airflux.reader.result.asFailure
import io.github.airflux.reader.result.asSuccess
import io.github.airflux.value.extension.readAsNumber

/**
 * Reader for primitive [Byte] type.
 */
fun buildByteReader(
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    valueCastErrorBuilder: ValueCastErrorBuilder
): JsReader<Byte> =
    JsReader { _, location, input ->
        input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
            try {
                text.toByte().asSuccess(location = p)
            } catch (expected: NumberFormatException) {
                valueCastErrorBuilder.build(text, Byte::class).asFailure(location = p)
            }
        }
    }
