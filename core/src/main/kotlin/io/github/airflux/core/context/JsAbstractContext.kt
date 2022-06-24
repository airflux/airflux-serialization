package io.github.airflux.core.context

public abstract class JsAbstractContext : JsContext {

    protected abstract val elements: Map<JsContext.Key<*>, JsContext.Element>

    override val isEmpty: Boolean
        get() = elements.isEmpty()

    @Suppress("UNCHECKED_CAST")
    override fun <E : JsContext.Element> getOrNull(key: JsContext.Key<E>): E? = elements[key]?.let { it as E }

    override operator fun <E : JsContext.Element> contains(key: JsContext.Key<E>): Boolean = elements.contains(key)

    protected fun <E : JsContext.Element> add(element: E): Map<JsContext.Key<*>, JsContext.Element> =
        elements + (element.key to element)

    protected fun <E : JsContext.Element> add(elements: Iterable<E>): Map<JsContext.Key<*>, JsContext.Element> =
        this.elements + elements.map { it.key to it }
}
