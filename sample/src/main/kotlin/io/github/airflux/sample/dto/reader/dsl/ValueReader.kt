package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.dto.reader.dsl.base.reader

val ValueReader = reader<Value> {
    val amount = property(name = "amount", reader = AmountReader).required()
    val currency = property(name = "currency", reader = CurrencyReader).required()

    build { values ->
        Value(
            amount = values[amount],
            currency = values[currency]
        )
    }
}
