package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.success
import io.github.airflux.dsl.reader.`object`.builder.property.specification.required
import io.github.airflux.dsl.reader.`object`.builder.validator.and
import io.github.airflux.dsl.reader.reader
import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.std.validator.`object`.ObjectValidator.additionalProperties
import io.github.airflux.std.validator.`object`.ObjectValidator.maxProperties
import io.github.airflux.std.validator.`object`.ObjectValidator.minProperties

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
