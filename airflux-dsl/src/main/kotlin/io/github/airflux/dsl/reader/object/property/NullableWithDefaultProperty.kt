package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.filter.JsPredicate
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.value.JsValue

sealed interface NullableWithDefaultProperty<T : Any> : JsReaderProperty {

    fun read(context: JsReaderContext?, path: JsResultPath, input: JsValue): JsResult<T?>

    fun <E : JsError> validation(validator: JsPropertyValidator<T?, E>): NullableWithDefaultProperty<T>

    fun filter(predicate: JsPredicate<T>): NullableWithDefaultProperty<T>
}
