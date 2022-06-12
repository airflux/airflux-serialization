package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.dsl.reader.`object`.validator.and
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.additionalProperties
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.isNotEmpty
import io.github.airflux.dsl.reader.`object`.validator.std.ObjectValidator.maxProperties
import io.github.airflux.dsl.reader.configuration.JsArrayReaderConfiguration
import io.github.airflux.dsl.reader.configuration.objectReaderConfiguration

val ObjectReaderConfiguration = objectReaderConfiguration {

    checkUniquePropertyPath = true

    validation {
        before = additionalProperties
        after = isNotEmpty and maxProperties(10)
    }
}

val ArrayReaderConfiguration = JsArrayReaderConfiguration.DEFAULT
