package io.github.airflux.reader.extension

import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue

fun JsValue.readAsBoolean(invalidTypeErrorBuilder: InvalidTypeErrorBuilder) =
    when (this) {
        is JsBoolean -> JsResult.Success(this.underlying)
        else -> JsResult.Failure(error = invalidTypeErrorBuilder.build(JsValue.Type.BOOLEAN, this.type))
    }

fun JsValue.readAsString(invalidTypeErrorBuilder: InvalidTypeErrorBuilder) =
    when (this) {
        is JsString -> JsResult.Success(this.underlying)
        else -> JsResult.Failure(error = invalidTypeErrorBuilder.build(JsValue.Type.STRING, this.type))
    }

fun <T : Number> JsValue.readAsNumber(
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    reader: (String) -> JsResult<T>
) =
    when (this) {
        is JsNumber -> reader(this.underlying)
        else -> JsResult.Failure(error = invalidTypeErrorBuilder.build(JsValue.Type.NUMBER, this.type))
    }

fun <T> JsValue.readAsObject(invalidTypeErrorBuilder: InvalidTypeErrorBuilder, reader: (JsObject) -> JsResult<T>) =
    when (this) {
        is JsObject -> reader(this)
        else -> JsResult.Failure(error = invalidTypeErrorBuilder.build(JsValue.Type.OBJECT, this.type))
    }
