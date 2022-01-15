package io.github.airflux.core.reader.base

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.value.extension.readAsBoolean

/**
 * Reader for primitive [Boolean] type.
 */
fun buildBooleanReader(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<Boolean> =
    JsReader { _, location, input -> input.readAsBoolean(location, invalidTypeErrorBuilder) }
