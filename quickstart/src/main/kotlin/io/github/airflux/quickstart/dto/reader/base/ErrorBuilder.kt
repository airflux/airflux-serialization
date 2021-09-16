package io.github.airflux.quickstart.dto.reader.base

import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.error.PathMissingErrorBuilder
import io.github.airflux.quickstart.json.error.JsonErrors

object ErrorBuilder {
    val PathMissing = PathMissingErrorBuilder { JsonErrors.PathMissing }
    val InvalidType = InvalidTypeErrorBuilder { expected, actual ->
        JsonErrors.InvalidType(expected, actual)
    }
}
