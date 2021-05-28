package io.github.airflux.reader.context

class JsReaderContext {
    private val map = mutableMapOf<Element.Key<*>, Element>()

    operator fun plus(element: Element): JsReaderContext {
        map[element.key] = element
        return this
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <E : Element> get(key: Element.Key<E>): E? = map[key]?.let { it as E }

    sealed interface Element {

        val key: Key<*>

        @Suppress("unused")
        interface Key<E : Element>
    }
}
