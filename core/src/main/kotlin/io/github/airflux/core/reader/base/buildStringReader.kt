package io.github.airflux.core.reader.base

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.value.extension.readAsString

/**
 * Reader for primitive [String] type.
 */
fun buildStringReader(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<String> =
    JsReader { _, location, input -> input.readAsString(location, invalidTypeErrorBuilder) }
