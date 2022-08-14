package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.model.Lots
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.result.withCatching
import io.github.airflux.serialization.dsl.reader.array.builder.arrayReader
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.nonNullable
import io.github.airflux.serialization.dsl.reader.array.builder.returns
import io.github.airflux.serialization.std.validator.array.StdArrayValidator.isNotEmpty

val LotsReader = arrayReader(ArrayReaderConfiguration) {
    validation {
        +isNotEmpty
    }
    returns(items = nonNullable(LotReader))
}.flatMap { context, location, items ->
    withCatching(context, location) {
        Lots(items).success()
    }
}
