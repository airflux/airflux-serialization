package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.model.Tender
import io.github.airflux.quickstart.dto.reader.dsl.property.identifierPropertySpec
import io.github.airflux.quickstart.dto.reader.dsl.validator.CommonObjectReaderValidators
import io.github.airflux.quickstart.dto.reader.env.ReaderCtx
import io.github.airflux.quickstart.dto.reader.env.ReaderErrorBuilders
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.optional
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.returns
import io.github.airflux.serialization.dsl.reader.struct.builder.structReader

val TenderReader: Reader<ReaderErrorBuilders, ReaderCtx, Tender> = structReader {
    validation {
        +CommonObjectReaderValidators
    }

    val id = property(identifierPropertySpec)
    val title = property(optional(name = "title", reader = TitleReader))
    val value = property(optional(name = "value", reader = ValueReader))
    val lots = property(required(name = "lots", reader = LotsReader))

    returns { _, _ ->
        Tender(+id, +title, +value, +lots).success()
    }
}
