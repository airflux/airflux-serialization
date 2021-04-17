package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.JsReader
import io.github.airflux.sample.dto.model.Value
import io.github.airflux.sample.dto.reader.dsl.base.DefaultObjectReaderConfig
import io.github.airflux.sample.dto.reader.dsl.base.DefaultObjectValidations
import io.github.airflux.sample.dto.reader.dsl.base.reader
import io.github.airflux.sample.dto.reader.dsl.base.simpleBuilder

val ValueReader: JsReader<Value> = reader(DefaultObjectReaderConfig, DefaultObjectValidations) {
    val amount = attribute(name = "amount", reader = AmountReader).required()
    val currency = attribute(name = "currency", reader = CurrencyReader).required()

    typeBuilder = simpleBuilder { values ->
        Value(
            amount = values[amount],
            currency = values[currency]
        )
    }
}
