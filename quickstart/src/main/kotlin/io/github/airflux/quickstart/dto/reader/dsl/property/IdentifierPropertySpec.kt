package io.github.airflux.quickstart.dto.reader.dsl.property

import io.github.airflux.core.reader.base.StringReader
import io.github.airflux.dsl.reader.`object`.property.path.or
import io.github.airflux.dsl.reader.`object`.property.specification.required
import io.github.airflux.quickstart.dto.reader.validator.StringValidator

val identifierPropertySpec =
    required("id" or "identifier", reader = StringReader)
        .validation(StringValidator.isNotBlank)
