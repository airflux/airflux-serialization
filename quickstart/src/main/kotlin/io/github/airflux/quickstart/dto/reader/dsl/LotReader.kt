package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.base.StringReader
import io.github.airflux.core.reader.result.success
import io.github.airflux.core.reader.validation
import io.github.airflux.core.reader.validator.std.string.StringValidator.isNotBlank
import io.github.airflux.dsl.reader.`object`.property.specification.required
import io.github.airflux.dsl.reader.`object`.validator.and
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.additionalProperties
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.isNotEmpty
import io.github.airflux.dsl.reader.reader
import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.reader.base.asEnum
import io.github.airflux.quickstart.dto.reader.dsl.property.identifierPropertySpec

val LotStatusReader = StringReader.validation(isNotBlank).asEnum<LotStatus>()

val LotReader = reader<Lot>(ObjectReaderConfiguration) {

    checkUniquePropertyPath = false

    validation {
        before = before and additionalProperties
        after = isNotEmpty
    }

    val id = property(identifierPropertySpec)
    val status = property(required(name = "status", reader = LotStatusReader))
    val value = property(required(name = "value", reader = ValueReader))

    returns { _, location ->
        Lot(id = +id, status = +status, value = +value).success(location)
    }
}

