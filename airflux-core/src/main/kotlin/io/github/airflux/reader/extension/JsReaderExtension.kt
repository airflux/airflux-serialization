package io.github.airflux.reader.extension

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue

fun JsValue.readAsBoolean(errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError) = when (this) {
    is JsBoolean -> JsResult.Success(this.underlying)
    else -> JsResult.Failure(error = errorInvalidType(JsValue.Type.BOOLEAN, this.type))
}

fun JsValue.readAsString(errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError) = when (this) {
    is JsString -> JsResult.Success(this.underlying)
    else -> JsResult.Failure(error = errorInvalidType(JsValue.Type.STRING, this.type))
}

fun <T : Number> JsValue.readAsNumber(
    errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError,
    transformer: (String) -> JsResult<T>
) = when (this) {
    is JsNumber -> transformer(this.underlying)
    else -> JsResult.Failure(error = errorInvalidType(JsValue.Type.NUMBER, this.type))
}

fun<T> JsValue.readAsObject(
    errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> JsError,
    reader: (JsObject) -> JsResult<T>
) = when (this) {
    is JsObject -> reader(this)
    else -> JsResult.Failure(error = errorInvalidType(JsValue.Type.OBJECT, this.type))
}
