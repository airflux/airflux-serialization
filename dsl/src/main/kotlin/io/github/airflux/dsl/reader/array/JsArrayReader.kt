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
public fun interface JsArrayReader<T> : JsReader<List<T>> {

    public fun interface ResultBuilder<T> : (JsReaderContext, JsLocation, JsArray<*>) -> JsResult<List<T>>

    @AirfluxMarker
    public interface Builder<T> {
        public fun validation(block: Validation.Builder<T>.() -> Unit)

        public fun returns(items: JsArrayItemSpec<T>): ResultBuilder<T>
        public fun returns(prefixItems: JsArrayPrefixItemsSpec<T>, items: Boolean): ResultBuilder<T>
        public fun returns(prefixItems: JsArrayPrefixItemsSpec<T>, items: JsArrayItemSpec<T>): ResultBuilder<T>
    }

    public class Validation<T> private constructor(
        public val before: JsArrayValidatorBuilder.Before?,
        public val after: JsArrayValidatorBuilder.After<T>?
    ) {

        @AirfluxMarker
        public class Builder<T>(
            public var before: JsArrayValidatorBuilder.Before? = null,
            public var after: JsArrayValidatorBuilder.After<T>? = null
        ) {
            internal fun build(): Validation<T> = Validation(before, after)
        }
    }
}
