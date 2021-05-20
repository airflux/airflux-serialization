package io.github.airflux.sample.dto.writer.base

import io.github.airflux.dsl.writer.`object`.ObjectWriter
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration

val DefaultObjectWriterConfiguration = ObjectWriterConfiguration.build {
    skipPropertyIfArrayIsEmpty = true
}

val writer = ObjectWriter(DefaultObjectWriterConfiguration)
