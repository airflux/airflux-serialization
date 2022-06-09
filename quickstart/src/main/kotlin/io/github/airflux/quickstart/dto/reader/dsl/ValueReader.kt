package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.success
import io.github.airflux.dsl.reader.`object`.property.specification.required
import io.github.airflux.dsl.reader.`object`.validator.and
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.additionalProperties
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.maxProperties
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.minProperties
import io.github.airflux.dsl.reader.reader
import io.github.airflux.quickstart.dto.model.Value

val ValueReader = reader<Value>(ObjectReaderConfiguration) {
    validation {
        before = additionalProperties
        after = minProperties(2) and maxProperties(2)
    }

    val amount = property(required(name = "amount", reader = AmountReader))
    val currency = property(required(name = "currency", reader = CurrencyReader))

    returns { _, location ->
        Value(amount = +amount, currency = +currency).success(location)
    }
}
