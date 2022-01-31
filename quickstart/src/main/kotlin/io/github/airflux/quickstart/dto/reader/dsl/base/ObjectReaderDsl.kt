package io.github.airflux.quickstart.dto.reader.dsl.base

import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.dsl.reader.`object`.createObjectReader
import io.github.airflux.quickstart.dto.reader.base.ErrorBuilder

private val GlobalObjectReaderConfig = ObjectReaderConfiguration.build {
    failFast = true
}

val reader = createObjectReader(
    configuration = GlobalObjectReaderConfig,
    pathMissingErrorBuilder = ErrorBuilder.PathMissing,
    invalidTypeErrorBuilder = ErrorBuilder.InvalidType
)
