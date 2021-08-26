package io.github.airflux.reader.context

sealed interface JsReaderContext {
    operator fun <E : Element> plus(element: E): JsReaderContext
    operator fun <E : Element> get(key: Key<E>): E?
    operator fun <E : Element> contains(key: Key<E>): Boolean

    @Suppress("unused")
    interface Key<E : Element>

    interface Element {
        val key: Key<*>

        operator fun <E : Element> plus(element: E): List<Element> = listOf(this, element)
    }

    object Empty : JsReaderContext {
        override fun <E : Element> plus(element: E): JsReaderContext = JsReaderContextInstance(element)
        override fun <E : Element> get(key: Key<E>): E? = null
        override fun <E : Element> contains(key: Key<E>): Boolean = false
    }

    private class JsReaderContextInstance(private val elements: Map<Key<*>, Element>) : JsReaderContext {

        constructor(element: Element) : this(mapOf(element.key to element))
        constructor(elements: Collection<Element>) : this(elements.associateBy { it.key })

        override fun <E : Element> plus(element: E): JsReaderContext =
            JsReaderContextInstance(this.elements + (element.key to element))

        override operator fun <E : Element> get(key: Key<E>): E? =
            @Suppress("UNCHECKED_CAST")
            elements[key]?.let { it as E }

        override operator fun <E : Element> contains(key: Key<E>): Boolean = elements.contains(key)
    }

    companion object {
        operator fun invoke(element: Element): JsReaderContext = JsReaderContextInstance(element)
        operator fun invoke(elements: Collection<Element>): JsReaderContext = JsReaderContextInstance(elements)
    }
}
