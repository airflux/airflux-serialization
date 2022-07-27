package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.serialization.dsl.reader.array.builder.arrayReader
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.nonNullable
import io.github.airflux.serialization.dsl.reader.array.builder.returns
import io.github.airflux.serialization.std.validator.array.ArrayValidator.isNotEmpty

val LotsReader = arrayReader<Lot>(ArrayReaderConfiguration) {
    validation {
        +isNotEmpty
    }
    returns(items = nonNullable(LotReader))
}
