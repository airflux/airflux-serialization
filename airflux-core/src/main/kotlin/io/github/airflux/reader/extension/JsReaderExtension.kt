package io.github.airflux.reader.extension

import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue

fun JsValue.readAsBoolean(currentPath: JsResultPath, invalidTypeErrorBuilder: InvalidTypeErrorBuilder) =
    when (this) {
        is JsBoolean -> JsResult.Success(this.underlying, path = currentPath)
        else ->
            JsResult.Failure(path = currentPath, error = invalidTypeErrorBuilder.build(JsValue.Type.BOOLEAN, this.type))
    }

fun JsValue.readAsString(currentPath: JsResultPath, invalidTypeErrorBuilder: InvalidTypeErrorBuilder) =
    when (this) {
        is JsString -> JsResult.Success(this.underlying, path = currentPath)
        else ->
            JsResult.Failure(path = currentPath, error = invalidTypeErrorBuilder.build(JsValue.Type.STRING, this.type))
    }

fun <T : Number> JsValue.readAsNumber(
    currentPath: JsResultPath,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    reader: (JsResultPath, String) -> JsResult<T>
) =
    when (this) {
        is JsNumber -> reader(currentPath, this.underlying)
        else ->
            JsResult.Failure(path = currentPath, error = invalidTypeErrorBuilder.build(JsValue.Type.NUMBER, this.type))
    }

fun <T> JsValue.readAsObject(
    currentPath: JsResultPath,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    reader: (JsResultPath, JsObject) -> JsResult<T>
) =
    when (this) {
        is JsObject -> reader(currentPath, this)
        else ->
            JsResult.Failure(path = currentPath, error = invalidTypeErrorBuilder.build(JsValue.Type.OBJECT, this.type))
    }
