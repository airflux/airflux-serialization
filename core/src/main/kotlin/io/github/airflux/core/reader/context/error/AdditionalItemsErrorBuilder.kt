package io.github.airflux.core.reader.context.error

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError

class AdditionalItemsErrorBuilder(private val builder: () -> JsError) :
    AbstractErrorBuilderContextElement<AdditionalItemsErrorBuilder>(key = AdditionalItemsErrorBuilder) {

    fun build(): JsError = builder()

    companion object Key : JsReaderContext.Key<AdditionalItemsErrorBuilder> {
        override val name: String = "AdditionalItemsErrorBuilder"
    }
}
