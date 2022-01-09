package io.github.airflux.quickstart.dto.reader.dsl.base

import io.github.airflux.dsl.reader.`object`.ObjectReader
import io.github.airflux.dsl.reader.`object`.ObjectReaderConfiguration
import io.github.airflux.quickstart.dto.reader.base.ErrorBuilder

private val GlobalObjectReaderConfig = ObjectReaderConfiguration.build {
    failFast = true
}

val reader = ObjectReader(
    globalConfiguration = GlobalObjectReaderConfig,
    pathMissingErrorBuilder = ErrorBuilder.PathMissing,
    invalidTypeErrorBuilder = ErrorBuilder.InvalidType
)
