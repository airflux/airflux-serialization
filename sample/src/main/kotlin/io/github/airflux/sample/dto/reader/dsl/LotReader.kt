package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.model.Lot
import io.github.airflux.sample.dto.model.LotStatus
import io.github.airflux.sample.dto.reader.base.CollectionReader.list
import io.github.airflux.sample.dto.reader.base.EnumReader
import io.github.airflux.sample.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.sample.dto.reader.dsl.base.reader
import io.github.airflux.sample.dto.reader.dsl.base.simpleBuilder
import io.github.airflux.sample.json.validation.ArrayValidator.isUnique

val LotStatusReader = EnumReader.readAsEnum<LotStatus>()

val LotReader = reader<Lot> {
    val id = property(name = "id", reader = stringReader).required()
    val status = property(name = "status", reader = LotStatusReader).required()
    val value = property(name = "value", reader = ValueReader).required()

    typeBuilder = simpleBuilder { values ->
        Lot(
            id = values[id],
            status = values[status],
            value = values[value]
        )
    }
}

val LotsReader = list(LotReader)
    .validation(isUnique { lot -> lot.id })
