package io.github.airflux.sample.dto.reader.dsl

import io.github.airflux.reader.JsReader
import io.github.airflux.reader.validator.extension.validation
import io.github.airflux.sample.dto.model.Lot
import io.github.airflux.sample.dto.model.LotStatus
import io.github.airflux.sample.dto.reader.dsl.base.CollectionReader.list
import io.github.airflux.sample.dto.reader.dsl.base.DefaultObjectReaderConfig
import io.github.airflux.sample.dto.reader.dsl.base.DefaultObjectValidations
import io.github.airflux.sample.dto.reader.dsl.base.EnumReader
import io.github.airflux.sample.dto.reader.dsl.base.PrimitiveReader.stringReader
import io.github.airflux.sample.dto.reader.dsl.base.reader
import io.github.airflux.sample.dto.reader.dsl.base.simpleBuilder
import io.github.airflux.sample.json.validation.ArrayValidator.isUnique

val LotStatusReader: JsReader<LotStatus> = EnumReader.readAsEnum<LotStatus>()

val LotReader: JsReader<Lot> = reader(DefaultObjectReaderConfig, DefaultObjectValidations) {
    val id = attribute(name = "id", reader = stringReader).required()
    val status = attribute(name = "status", reader = LotStatusReader).required()
    val value = attribute(name = "value", reader = ValueReader).required()

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
