package io.github.airflux.quickstart.dto.writer.base

import io.github.airflux.core.writer.`object`.ObjectWriter
import io.github.airflux.dsl.writer.config.ObjectWriterConfig

val DefaultObjectWriterConfiguration = ObjectWriterConfig.build {
    skipPropertyIfArrayIsEmpty = true
}

val writer = ObjectWriter(DefaultObjectWriterConfiguration)
