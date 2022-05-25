package io.github.airflux.quickstart.dto.reader.context

import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.dsl.reader.context.JsReaderContextBuilder
import io.github.airflux.dsl.reader.context.readerContext
import io.github.airflux.quickstart.json.error.JsonErrors

val DefaultReaderContext = readerContext {
    failFast = false

    errorBuilders {
        readerErrorBuilders()
    }

    exceptions {
        exception<IllegalArgumentException> { _, _, _ -> JsonErrors.PathMissing }
        exception<Exception> { _, _, _ -> JsonErrors.PathMissing }
    }
}

fun JsReaderContextBuilder.ErrorsBuilder.readerErrorBuilders() {
    register(PathMissingErrorBuilder { JsonErrors.PathMissing })
    +InvalidTypeErrorBuilder(JsonErrors::InvalidType)
    +ValueCastErrorBuilder(JsonErrors::ValueCast)
}
