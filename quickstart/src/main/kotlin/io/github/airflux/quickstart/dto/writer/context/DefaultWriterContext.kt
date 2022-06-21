package io.github.airflux.quickstart.dto.writer.context

import io.github.airflux.core.writer.context.option.WriteActionIfArrayIsEmpty
import io.github.airflux.core.writer.context.option.WriteActionIfObjectIsEmpty
import io.github.airflux.dsl.writer.context.writerContext

val DefaultWriterContext = writerContext {
    writeActionIfObjectIsEmpty = WriteActionIfObjectIsEmpty.Action.SKIP
    writeActionIfArrayIsEmpty = WriteActionIfArrayIsEmpty.Action.EMPTY
}
