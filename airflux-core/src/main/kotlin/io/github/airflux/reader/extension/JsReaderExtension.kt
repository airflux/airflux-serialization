package io.github.airflux.reader.extension

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue

fun <E : JsError> JsValue.readAsBoolean(errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E) =
    when (this) {
        is JsBoolean -> JsResult.Success(this.underlying)
        else -> JsResult.Failure(error = errorInvalidType(JsValue.Type.BOOLEAN, this.type))
    }

fun <E : JsError> JsValue.readAsString(errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E) =
    when (this) {
        is JsString -> JsResult.Success(this.underlying)
        else -> JsResult.Failure(error = errorInvalidType(JsValue.Type.STRING, this.type))
    }

fun <T : Number, E : JsError> JsValue.readAsNumber(
    errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E,
    reader: (String) -> JsResult<T, E>
) = when (this) {
    is JsNumber -> reader(this.underlying)
    else -> JsResult.Failure(error = errorInvalidType(JsValue.Type.NUMBER, this.type))
}

fun <T, E : JsError> JsValue.readAsObject(
    errorInvalidType: (expected: JsValue.Type, actual: JsValue.Type) -> E,
    reader: (JsObject) -> JsResult<T, E>
) = when (this) {
    is JsObject -> reader(this)
    else -> JsResult.Failure(error = errorInvalidType(JsValue.Type.OBJECT, this.type))
}
