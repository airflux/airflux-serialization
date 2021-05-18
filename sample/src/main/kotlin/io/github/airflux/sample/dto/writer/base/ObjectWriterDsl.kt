package io.github.airflux.sample.dto.writer.base

import io.github.airflux.dsl.writer.`object`.ObjectWriter
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration
import io.github.airflux.dsl.writer.`object`.ObjectWriterConfiguration.WriteProperty.SKIP_PROPERTY_IF_ARRAY_IS_EMPTY

val DefaultObjectWriterConfiguration = ObjectWriterConfiguration(SKIP_PROPERTY_IF_ARRAY_IS_EMPTY)

val writer = ObjectWriter(DefaultObjectWriterConfiguration)
