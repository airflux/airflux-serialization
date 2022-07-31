package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.serialization.dsl.reader.config.ArrayReaderConfig
import io.github.airflux.serialization.dsl.reader.config.objectReaderConfig
import io.github.airflux.serialization.std.validator.struct.StdObjectValidator.additionalProperties
import io.github.airflux.serialization.std.validator.struct.StdObjectValidator.isNotEmpty
import io.github.airflux.serialization.std.validator.struct.StdObjectValidator.maxProperties

val ObjectReaderConfiguration = objectReaderConfig {

    validation {
        +additionalProperties
        +isNotEmpty
        +maxProperties(10)
    }
}

val ArrayReaderConfiguration = ArrayReaderConfig.DEFAULT
