package io.github.airflux.reader.error

import io.github.airflux.reader.result.JsError
import kotlin.reflect.KClass

fun interface ValueCastErrorBuilder {
    fun build(value: String, type: KClass<*>): JsError
}
