package io.github.airflux.quickstart.dto.reader.dsl.property

import io.github.airflux.quickstart.dto.reader.base.StringReader
import io.github.airflux.quickstart.dto.reader.base.isNotBlank
import io.github.airflux.serialization.dsl.path.or
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.validation

val identifierPropertySpec =
    required("id" or "identifier", reader = StringReader)
        .validation(isNotBlank)
