package io.github.airflux.reader.base

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.value.extension.readAsString

/**
 * Reader for primitive [String] type.
 */
fun buildStringReader(invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsReader<String> =
    JsReader { _, location, input -> input.readAsString(location, invalidTypeErrorBuilder) }
