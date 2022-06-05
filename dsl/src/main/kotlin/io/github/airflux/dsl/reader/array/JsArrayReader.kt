package io.github.airflux.dsl.reader.array

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsArray
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.array.item.specification.JsArrayItemSpec
import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder

@Suppress("unused")
fun interface JsArrayReader<T> : JsReader<List<T>> {

    fun interface ResultBuilder<T> : (JsReaderContext, JsLocation, JsArray<*>) -> JsResult<List<T>>

    @AirfluxMarker
    interface Builder<T> {
        fun validation(block: Validation.Builder<T>.() -> Unit)

        fun returns(items: JsArrayItemSpec<T>): ResultBuilder<T>
        fun returns(prefixItems: JsArrayPrefixItemsSpec<T>, items: Boolean): ResultBuilder<T>
        fun returns(prefixItems: JsArrayPrefixItemsSpec<T>, items: JsArrayItemSpec<T>): ResultBuilder<T>
    }

    class Validation<T> private constructor(
        val before: JsArrayValidatorBuilder.Before?,
        val after: JsArrayValidatorBuilder.After<T>?
    ) {

        @AirfluxMarker
        class Builder<T>(
            var before: JsArrayValidatorBuilder.Before? = null,
            var after: JsArrayValidatorBuilder.After<T>? = null
        ) {
            internal fun build(): Validation<T> = Validation(before, after)
        }
    }
}
