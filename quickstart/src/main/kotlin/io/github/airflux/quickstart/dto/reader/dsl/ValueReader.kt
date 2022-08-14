package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.model.Value
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.returns
import io.github.airflux.serialization.dsl.reader.struct.builder.structReader
import io.github.airflux.serialization.std.validator.struct.StdObjectValidator.additionalProperties
import io.github.airflux.serialization.std.validator.struct.StdObjectValidator.maxProperties
import io.github.airflux.serialization.std.validator.struct.StdObjectValidator.minProperties

val ValueReader = structReader<Value>(ObjectReaderConfiguration) {
    validation {
        +additionalProperties
        +minProperties(2)
        +maxProperties(2)
    }

    val amount = property(required(name = "amount", reader = AmountReader))
    val currency = property(required(name = "currency", reader = CurrencyReader))

    returns { _, _ ->
        Value(amount = +amount, currency = +currency).success()
    }
}
