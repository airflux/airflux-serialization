package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.`object`.builder.reader
import io.github.airflux.serialization.dsl.reader.`object`.builder.returns
import io.github.airflux.serialization.std.validator.`object`.ObjectValidator.additionalProperties
import io.github.airflux.serialization.std.validator.`object`.ObjectValidator.maxProperties
import io.github.airflux.serialization.std.validator.`object`.ObjectValidator.minProperties

val ValueReader = reader<Value>(ObjectReaderConfiguration) {
    validation {
        +additionalProperties
        +minProperties(2)
        +maxProperties(2)
    }

    val amount = property(required(name = "amount", reader = AmountReader))
    val currency = property(required(name = "currency", reader = CurrencyReader))

    returns { _, location ->
        Value(amount = +amount, currency = +currency).success(location)
    }
}
