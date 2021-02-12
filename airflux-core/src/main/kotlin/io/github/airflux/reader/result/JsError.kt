package io.github.airflux.reader.result

import io.github.airflux.value.JsValue
import kotlin.reflect.KClass

sealed class JsError {

    object PathMissing : JsError()

    data class InvalidType(val expected: JsValue.Type, val actual: JsValue.Type) : JsError()

    data class ValueCast(val value: String, val type: KClass<*>) : JsError()

    abstract class Validation : JsError()
}
