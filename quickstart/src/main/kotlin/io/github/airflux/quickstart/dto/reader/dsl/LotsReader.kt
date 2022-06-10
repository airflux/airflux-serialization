package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.dsl.reader.array.item.specification.nonNullable
import io.github.airflux.dsl.reader.array.validator.std.ArrayValidator.isNotEmpty
import io.github.airflux.dsl.reader.array.validator.std.ArrayValidator.isUnique
import io.github.airflux.dsl.reader.arrayReader
import io.github.airflux.quickstart.dto.model.Lot

val LotsReader = arrayReader<Lot>(ArrayReaderConfiguration) {
    validation {
        before = isNotEmpty
        after = isUnique { lot -> lot.id }
    }
    returns(items = nonNullable(LotReader))
}
