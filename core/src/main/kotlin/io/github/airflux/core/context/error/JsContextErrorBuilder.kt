package io.github.airflux.core.context.error

import io.github.airflux.core.context.JsContext
import kotlin.reflect.full.companionObject

public operator fun <E : JsContextErrorBuilderElement> JsContext.get(key: JsContextErrorBuilderKey<E>): E =
    getOrNull(key)
        ?: throw NoSuchElementException("The error builder '${key.name}' is missing in the context.")

public interface JsContextErrorBuilderKey<E : JsContextErrorBuilderElement> : JsContext.Key<E> {
    public val name: String
}

public interface JsContextErrorBuilderElement : JsContext.Element {
    override val key: JsContextErrorBuilderKey<*>
}

public inline fun <reified T : JsContextErrorBuilderKey<*>> T.errorBuilderName(): String =
    errorBuilderName(T::class.java)

public fun <T : JsContextErrorBuilderKey<*>> errorBuilderName(javaClass: Class<T>): String =
    (javaClass.enclosingClass?.takeIf { it.kotlin.companionObject?.java == javaClass } ?: javaClass).canonicalName
