package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.core.reader.result.success
import io.github.airflux.core.reader.validation
import io.github.airflux.dsl.reader.`object`.builder.property.specification.required
import io.github.airflux.dsl.reader.`object`.builder.validator.and
import io.github.airflux.dsl.reader.reader
import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.reader.base.asEnum
import io.github.airflux.quickstart.dto.reader.dsl.property.identifierPropertySpec
import io.github.airflux.std.reader.StringReader
import io.github.airflux.std.validator.`object`.ObjectValidator.additionalProperties
import io.github.airflux.std.validator.`object`.ObjectValidator.isNotEmpty
import io.github.airflux.std.validator.string.StringValidator.isNotBlank

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

