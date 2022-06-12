package io.github.airflux.core.reader.context

import kotlin.reflect.full.companionObject

public inline fun <reified T : JsReaderContext.Key<*>> T.contextKeyName(): String = contextKeyName(T::class.java)

public fun <T : JsReaderContext.Key<*>> contextKeyName(javaClass: Class<T>): String =
    (javaClass.enclosingClass?.takeIf { it.kotlin.companionObject?.java == javaClass } ?: javaClass).canonicalName
