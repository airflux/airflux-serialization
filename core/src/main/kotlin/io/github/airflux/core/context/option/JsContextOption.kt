package io.github.airflux.core.context.option

import io.github.airflux.core.context.JsContext

public fun <T, E : JsContextOptionElement<T>> JsContext.get(key: JsContextOptionKey<T, E>, default: () -> T): T =
    getOrNull(key)?.value ?: default()

public interface JsContextOptionKey<T, E : JsContextOptionElement<T>> : JsContext.Key<E>

public interface JsContextOptionElement<T> : JsContext.Element {
    override val key: JsContextOptionKey<T, *>
    public val value: T
}
