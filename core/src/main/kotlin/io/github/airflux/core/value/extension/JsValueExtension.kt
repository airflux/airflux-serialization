package io.github.airflux.core.value.extension

import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue

fun JsValue.readAsBoolean(location: JsLocation, invalidTypeErrorBuilder: InvalidTypeErrorBuilder) =
    when (this) {
        is JsBoolean -> JsResult.Success(location, this.get)
        else -> JsResult.Failure(
            location = location,
            error = invalidTypeErrorBuilder.build(JsValue.Type.BOOLEAN, this.type)
        )
    }

fun JsValue.readAsString(location: JsLocation, invalidTypeErrorBuilder: InvalidTypeErrorBuilder) =
    when (this) {
        is JsString -> JsResult.Success(location, this.get)
        else ->
            JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.STRING, this.type))
    }

fun <T : Number> JsValue.readAsNumber(
    location: JsLocation,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    reader: (JsLocation, String) -> JsResult<T>
) = when (this) {
    is JsNumber -> reader(location, this.get)
    else -> JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.NUMBER, this.type))
}

fun <T> JsValue.readAsObject(
    location: JsLocation,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    reader: (JsLocation, JsObject) -> JsResult<T>
) = when (this) {
    is JsObject -> reader(location, this)
    else -> JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.OBJECT, this.type))
}
