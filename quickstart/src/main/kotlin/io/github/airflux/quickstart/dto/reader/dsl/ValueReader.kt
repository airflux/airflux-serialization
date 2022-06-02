package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.success
import io.github.airflux.dsl.reader.`object`.property.specification.required
import io.github.airflux.dsl.reader.`object`.validator.and
import io.github.airflux.dsl.reader.`object`.validator.base.AdditionalProperties
import io.github.airflux.dsl.reader.`object`.validator.base.MaxProperties
import io.github.airflux.dsl.reader.`object`.validator.base.MinProperties
import io.github.airflux.dsl.reader.reader
import io.github.airflux.quickstart.dto.model.Value

val ValueReader = reader<Value>(ObjectReaderConfiguration) {
    validation {
        before = AdditionalProperties
        after = MinProperties(2) and MaxProperties(2)
    }

    val amount = property(required(name = "amount", reader = AmountReader))
    val currency = property(required(name = "currency", reader = CurrencyReader))

    returns { _, location ->
        Value(amount = +amount, currency = +currency).success(location)
    }
}
