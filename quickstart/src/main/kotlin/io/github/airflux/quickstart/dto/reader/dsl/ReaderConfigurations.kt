package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.serialization.dsl.reader.config.ArrayReaderConfig
import io.github.airflux.serialization.dsl.reader.config.objectReaderConfig
import io.github.airflux.serialization.std.validator.`object`.StdObjectValidator.additionalProperties
import io.github.airflux.serialization.std.validator.`object`.StdObjectValidator.isNotEmpty
import io.github.airflux.serialization.std.validator.`object`.StdObjectValidator.maxProperties

val ObjectReaderConfiguration = objectReaderConfig {

    validation {
        +additionalProperties
        +isNotEmpty
        +maxProperties(10)
    }
}

val ArrayReaderConfiguration = ArrayReaderConfig.DEFAULT
