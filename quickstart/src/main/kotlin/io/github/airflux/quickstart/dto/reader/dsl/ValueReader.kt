package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.quickstart.dto.reader.dsl.base.reader
import io.github.airflux.quickstart.json.validation.additionalProperties
import io.github.airflux.quickstart.json.validation.maxProperties
import io.github.airflux.quickstart.json.validation.minProperties
import io.github.airflux.reader.result.asSuccess

private val additionalPropertiesValidator = { _: ObjectReaderConfiguration, properties: List<JsReaderProperty> ->
    additionalProperties(properties)
}

val ValueReader = reader<Value> {
    configuration {
        failFast = false
    }

    validation {
        before(additionalPropertiesValidator)
        after { _, _ ->
            minProperties(2) and maxProperties(2)
        }
    }

    val amount = property(name = "amount", reader = AmountReader).required()
    val currency = property(name = "currency", reader = CurrencyReader).required()

    build { location ->
        Value(
            amount = +amount,
            currency = +currency
        ).asSuccess(location)
    }
}
