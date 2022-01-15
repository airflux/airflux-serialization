package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.reader.base.CollectionReader.list
import io.github.airflux.quickstart.dto.reader.base.PrimitiveReader.stringReader
import io.github.airflux.quickstart.dto.reader.base.asEnum
import io.github.airflux.quickstart.dto.reader.dsl.base.reader
import io.github.airflux.quickstart.json.validation.ArrayValidator.isUnique
import io.github.airflux.quickstart.json.validation.ArrayValidator.minItems
import io.github.airflux.quickstart.json.validation.StringValidator.isNotBlank
import io.github.airflux.quickstart.json.validation.additionalProperties
import io.github.airflux.quickstart.json.validation.isNotEmptyObject
import io.github.airflux.quickstart.json.validation.maxProperties
import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.core.reader.validator.extension.validation

val LotStatusReader = stringReader.validation(isNotBlank).asEnum<LotStatus>()

private val LotObjectReaderConfig = ObjectReaderConfiguration.build {
    failFast = false
}

val LotReader = reader<Lot>(configuration = LotObjectReaderConfig) {
    validation {
        before { _, properties ->
            additionalProperties(properties)
        }
        after { _, _ ->
            isNotEmptyObject and maxProperties(3)
        }
    }

    val id = property(name = "id", reader = stringReader).required()
    val status = property(name = "status", reader = LotStatusReader).required()
    val value = property(name = "value", reader = ValueReader).required()

    build { location ->
        Lot(
            id = +id,
            status = +status,
            value = +value
        ).asSuccess(location)
    }
}

val LotsReader = list(LotReader)
    .validation(minItems<Lot, List<Lot>>(1) and isUnique { lot: Lot -> lot.id })
