package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.predicate.JsPredicate
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.value.JsValue

sealed interface OptionalProperty<T : Any> : JsReaderProperty {

    fun read(context: JsReaderContext, location: JsLocation, input: JsValue): JsResult<T?>

    fun validation(validator: JsPropertyValidator<T?>): OptionalProperty<T>

    fun filter(predicate: JsPredicate<T>): OptionalProperty<T>
}
