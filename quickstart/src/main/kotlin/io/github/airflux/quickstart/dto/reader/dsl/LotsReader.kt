package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.model.Lots
import io.github.airflux.quickstart.dto.reader.dsl.validator.CommonArrayReaderValidators
import io.github.airflux.quickstart.dto.reader.env.ReaderCtx
import io.github.airflux.quickstart.dto.reader.env.ReaderErrorBuilders
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.flatMapResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.result.withCatching
import io.github.airflux.serialization.dsl.reader.array.builder.arrayReader
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.nonNullable
import io.github.airflux.serialization.dsl.reader.array.builder.returns

val LotsReader: Reader<ReaderErrorBuilders, ReaderCtx, Lots> = arrayReader {
    validation {
        +CommonArrayReaderValidators
    }
    returns(items = nonNullable(LotReader))
}.flatMapResult { env, location, items ->
    withCatching(env, location) {
        Lots(items).success()
    }
}
