package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.specification.builder.required
import io.github.airflux.dsl.reader.reader
import io.github.airflux.dsl.reader.scope.JsObjectReaderScope
import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.quickstart.dto.reader.validator.additionalProperties
import io.github.airflux.quickstart.dto.reader.validator.maxProperties
import io.github.airflux.quickstart.dto.reader.validator.minProperties

private val additionalPropertiesValidator = { _: JsObjectReaderScope, properties: List<JsReaderProperty> ->
    additionalProperties(properties)
}

val ValueReader = ObjectReaderScope.reader<Value> {
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
