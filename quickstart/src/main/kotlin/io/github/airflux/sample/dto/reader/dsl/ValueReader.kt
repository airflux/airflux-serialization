package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.result.asSuccess
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.dto.reader.dsl.base.reader
import io.github.airflux.sample.json.validation.additionalProperties as additionalProperties1

val ValueReader = reader<Value> {
    configuration {
        failFast = false
    }

    validation {
        +additionalProperties1
    }

    val amount = property(name = "amount", reader = AmountReader).required()
    val currency = property(name = "currency", reader = CurrencyReader).required()

    build { path ->
        Value(
            amount = +amount,
            currency = +currency
        ).asSuccess(path)
    }
}
