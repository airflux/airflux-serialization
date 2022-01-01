package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.result.asSuccess
import io.github.airflux.value.extension.readAsNumber
import java.math.BigDecimal

/**
 * Reader for [BigDecimal] type.
 */
fun buildBigDecimalReader(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<BigDecimal> =
    JsReader { _, location, input ->
        input.readAsNumber(location, invalidTypeErrorBuilder) { p, text ->
            BigDecimal(text).asSuccess(location = p)
        }
    }
