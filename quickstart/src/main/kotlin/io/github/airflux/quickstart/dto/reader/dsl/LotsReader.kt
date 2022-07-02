package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.dsl.reader.array.builder.arrayReader
import io.github.airflux.dsl.reader.array.builder.item.specification.nonNullable
import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.std.validator.array.ArrayValidator.isNotEmpty
import io.github.airflux.std.validator.array.ArrayValidator.isUnique

val LotsReader = arrayReader<Lot>(ArrayReaderConfiguration) {
    validation {
        before = isNotEmpty
        after = isUnique { lot -> lot.id }
    }
    returns(items = nonNullable(LotReader))
}
