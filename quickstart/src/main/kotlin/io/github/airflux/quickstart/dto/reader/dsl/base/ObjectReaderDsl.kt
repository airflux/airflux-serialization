package io.github.airflux.quickstart.dto.reader.dsl.base

import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.dsl.reader.JsReaderBuilder
import io.github.airflux.quickstart.dto.reader.base.ErrorBuilder
import io.github.airflux.quickstart.json.error.JsonErrors

val readerBuilderConfig = JsReaderBuilder.Configuration.build(ErrorBuilder.PathMissing, ErrorBuilder.InvalidType) {

    failFast = false
    exception<Exception> { location, _ ->
        JsResult.Failure(location, JsonErrors.PathMissing)
    }
}
