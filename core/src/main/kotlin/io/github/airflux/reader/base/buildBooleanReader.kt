package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.value.extension.readAsBoolean

/**
 * Reader for primitive [Boolean] type.
 */
fun buildBooleanReader(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<Boolean> =
    JsReader { _, location, input -> input.readAsBoolean(location, invalidTypeErrorBuilder) }
