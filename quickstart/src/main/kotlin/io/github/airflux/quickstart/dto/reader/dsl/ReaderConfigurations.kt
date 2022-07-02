package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.dsl.reader.config.JsArrayReaderConfig
import io.github.airflux.dsl.reader.config.objectReaderConfig
import io.github.airflux.dsl.reader.`object`.builder.validator.and
import io.github.airflux.std.validator.`object`.ObjectValidator.additionalProperties
import io.github.airflux.std.validator.`object`.ObjectValidator.isNotEmpty
import io.github.airflux.std.validator.`object`.ObjectValidator.maxProperties

val ObjectReaderConfiguration = objectReaderConfig {

    validation {
        before = additionalProperties
        after = isNotEmpty and maxProperties(10)
    }
}

val ArrayReaderConfiguration = JsArrayReaderConfig.DEFAULT
