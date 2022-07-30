package io.github.airflux.quickstart.dto.reader.dsl.property

import io.github.airflux.serialization.dsl.path.or
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.validation
import io.github.airflux.serialization.std.reader.StringReader
import io.github.airflux.serialization.std.validator.string.StdStringValidator.isNotBlank

val identifierPropertySpec =
    required("id" or "identifier", reader = StringReader)
        .validation(isNotBlank)
