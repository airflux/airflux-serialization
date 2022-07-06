package io.github.airflux.quickstart.dto.reader.dsl.property

import io.github.airflux.dsl.path.or
import io.github.airflux.dsl.reader.`object`.builder.property.specification.required
import io.github.airflux.dsl.reader.`object`.builder.property.specification.validate
import io.github.airflux.std.reader.StringReader
import io.github.airflux.std.validator.string.StringValidator.isNotBlank

val identifierPropertySpec =
    required("id" or "identifier", reader = StringReader)
        .validate(isNotBlank)
