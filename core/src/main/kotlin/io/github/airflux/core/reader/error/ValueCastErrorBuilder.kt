package io.github.airflux.core.reader.error

import io.github.airflux.core.reader.result.JsError
import kotlin.reflect.KClass

fun interface ValueCastErrorBuilder {
    fun build(value: String, type: KClass<*>): JsError
}
