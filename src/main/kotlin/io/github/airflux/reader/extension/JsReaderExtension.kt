package io.github.airflux.reader.extension

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue

fun JsValue.readAsBoolean() = when (this) {
    is JsBoolean -> JsResult.Success(this.underlying)
    else -> JsResult.Failure(error = JsError.InvalidType(expected = JsValue.Type.BOOLEAN, actual = this.type))
}

fun JsValue.readAsString() = when (this) {
    is JsString -> JsResult.Success(this.underlying)
    else -> JsResult.Failure(error = JsError.InvalidType(expected = JsValue.Type.STRING, actual = this.type))
}

fun <T : Number> JsValue.readAsNumber(transformer: (String) -> JsResult<T>) = when (this) {
    is JsNumber -> transformer(this.underlying)
    else -> JsResult.Failure(error = JsError.InvalidType(expected = JsValue.Type.NUMBER, actual = this.type))
}
