package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.base.StringReader
import io.github.airflux.core.reader.result.asSuccess
import io.github.airflux.core.reader.validator.and
import io.github.airflux.core.reader.validator.extension.validation
import io.github.airflux.dsl.reader.`object`.property.specification.builder.required
import io.github.airflux.dsl.reader.reader
import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.reader.base.CollectionReader.list
import io.github.airflux.quickstart.dto.reader.base.asEnum
import io.github.airflux.quickstart.dto.reader.validator.ArrayValidator.isUnique
import io.github.airflux.quickstart.dto.reader.validator.ArrayValidator.minItems
import io.github.airflux.quickstart.dto.reader.validator.StringValidator.isNotBlank
import io.github.airflux.quickstart.dto.reader.validator.additionalProperties
import io.github.airflux.quickstart.dto.reader.validator.isNotEmptyObject
import io.github.airflux.quickstart.dto.reader.validator.maxProperties

val LotStatusReader = StringReader.validation(isNotBlank).asEnum<LotStatus>()

val LotReader = ObjectReaderScope.reader<Lot> {
    validation {
        before { _, properties ->
            additionalProperties(properties)
        }
        after { _, _ ->
            isNotEmptyObject and maxProperties(3)
        }
    }

    val id = property(required(name = "id", reader = StringReader))
    val status = property(required(name = "status", reader = LotStatusReader))
    val value = property(required(name = "value", reader = ValueReader))

    build {
        Lot(id = +id, status = +status, value = +value).asSuccess(location)
    }
}

val LotsReader = list(LotReader)
    .validation(minItems<Lot, List<Lot>>(1) and isUnique { lot: Lot -> lot.id })
