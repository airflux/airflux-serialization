package io.github.airflux.quickstart.dto.writer.base

import io.github.airflux.dsl.writer.`object`.ObjectWriter
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration

val DefaultObjectWriterConfiguration = ObjectWriterConfiguration.build {
    skipPropertyIfArrayIsEmpty = true
}

val writer = ObjectWriter(DefaultObjectWriterConfiguration)
