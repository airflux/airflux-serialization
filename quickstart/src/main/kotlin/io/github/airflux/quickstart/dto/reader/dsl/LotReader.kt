package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.reader.base.asEnum
import io.github.airflux.quickstart.dto.reader.dsl.property.identifierPropertySpec
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.validation
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.returns
import io.github.airflux.serialization.dsl.reader.struct.builder.structReader
import io.github.airflux.serialization.std.reader.StringReader
import io.github.airflux.serialization.std.validator.string.StdStringValidator.isNotBlank
import io.github.airflux.serialization.std.validator.struct.StdObjectValidator.additionalProperties
import io.github.airflux.serialization.std.validator.struct.StdObjectValidator.isNotEmpty

val LotStatusReader = StringReader.validation(isNotBlank).asEnum<LotStatus>()

val LotReader = structReader<Lot>(ObjectReaderConfiguration) {

    validation {
        +additionalProperties
        +isNotEmpty
    }

    val id = property(identifierPropertySpec)
    val status = property(required(name = "status", reader = LotStatusReader))
    val value = property(required(name = "value", reader = ValueReader))

    returns { _, _ ->
        Lot(id = +id, status = +status, value = +value).success()
    }
}

