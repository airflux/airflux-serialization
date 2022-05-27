package io.github.airflux.quickstart.dto.reader.context

import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.dsl.reader.context.JsReaderContextBuilder
import io.github.airflux.dsl.reader.context.readerContext
import io.github.airflux.dsl.reader.`object`.validator.base.AdditionalProperties
import io.github.airflux.dsl.reader.`object`.validator.base.IsNotEmpty
import io.github.airflux.dsl.reader.`object`.validator.base.MaxProperties
import io.github.airflux.dsl.reader.`object`.validator.base.MinProperties
import io.github.airflux.quickstart.json.error.JsonErrors

val DefaultReaderContext = readerContext {
    failFast = false

    errorBuilders {
        readerErrorBuilders()
        objectValidationErrorBuilders()
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

fun JsReaderContextBuilder.ErrorsBuilder.objectValidationErrorBuilders() {
    +AdditionalProperties.Error(JsonErrors.Validation.Object::AdditionalProperties)
    +IsNotEmpty.Error { JsonErrors.Validation.Object.IsEmpty }
    +MinProperties.Error(JsonErrors.Validation.Object::MinProperties)
    +MaxProperties.Error(JsonErrors.Validation.Object::MaxProperties)
}
