package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.value.JsValue

sealed interface DefaultableProperty<T : Any> : JsReaderProperty<T> {

    fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T>

    fun <E : JsError> validation(validator: JsValidator<T, E>): DefaultableProperty<T>
}
