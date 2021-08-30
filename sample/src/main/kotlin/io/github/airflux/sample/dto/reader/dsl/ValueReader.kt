package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.result.asSuccess
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.dto.reader.dsl.base.reader

val ValueReader = reader<Value> {
    val amount = property(name = "amount", reader = AmountReader).required()
    val currency = property(name = "currency", reader = CurrencyReader).required()

    build { path ->
        Value(
            amount = this[amount],
            currency = this[currency]
        ).asSuccess(path)
    }
}
