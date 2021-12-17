package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.reader.result.asSuccess
import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.quickstart.dto.reader.dsl.base.reader
import io.github.airflux.quickstart.json.validation.additionalProperties as additionalProperties1

val ValueReader = reader<Value> {
    configuration {
        failFast = false
    }

    validation {
        +additionalProperties1
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
