package io.github.airflux.dsl.reader.`object`

import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath

fun interface TypeBuilder<T> : (ObjectValuesMap, JsResultPath) -> JsResult<T>
