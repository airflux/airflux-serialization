package io.github.airflux.quickstart.dto.writer.context

import io.github.airflux.core.writer.context.option.ActionOfWriterIfArrayIsEmpty
import io.github.airflux.core.writer.context.option.ActionOfWriterIfObjectIsEmpty
import io.github.airflux.dsl.writer.context.writerContext

val DefaultWriterContext = writerContext {
    actionOfWriterIfObjectIsEmpty = ActionOfWriterIfObjectIsEmpty.Action.SKIP
    actionOfWriterIfArrayIsEmpty = ActionOfWriterIfArrayIsEmpty.Action.NONE
}
