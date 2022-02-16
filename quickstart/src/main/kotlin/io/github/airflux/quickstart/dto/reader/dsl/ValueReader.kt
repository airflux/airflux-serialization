package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.dsl.reader.JsReaderBuilder
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.specification.builder.required
import io.github.airflux.dsl.reader.objectReaderOf
import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.quickstart.dto.reader.dsl.base.readerBuilderConfig
import io.github.airflux.quickstart.json.validation.additionalProperties
import io.github.airflux.quickstart.json.validation.maxProperties
import io.github.airflux.quickstart.json.validation.minProperties

private val additionalPropertiesValidator = { _: JsReaderBuilder.Options, properties: List<JsReaderProperty> ->
    additionalProperties(properties)
}

val ValueReader = objectReaderOf<Value>(readerBuilderConfig) {
    validation {
        before(additionalPropertiesValidator)
        after { _, _ ->
            minProperties(2) and maxProperties(2)
        }
    }

    val amount = property(required(name = "amount", reader = AmountReader))
    val currency = property(required(name = "currency", reader = CurrencyReader))

    build {
        Value(amount = +amount, currency = +currency).asSuccess(location)
    }
}
