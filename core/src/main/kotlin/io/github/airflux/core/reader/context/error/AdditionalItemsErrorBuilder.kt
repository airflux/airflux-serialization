package io.github.airflux.core.reader.context.error

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError

public class AdditionalItemsErrorBuilder(private val builder: () -> JsError) :
    AbstractErrorBuilderContextElement<AdditionalItemsErrorBuilder>(key = AdditionalItemsErrorBuilder) {

    public fun build(): JsError = builder()

    public companion object Key : JsReaderContext.Key<AdditionalItemsErrorBuilder> {
        override val name: String = "AdditionalItemsErrorBuilder"
    }
}
