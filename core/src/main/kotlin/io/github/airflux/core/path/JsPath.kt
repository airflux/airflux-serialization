package io.github.airflux.core.path

@Suppress("unused")
class JsPath private constructor(private val elements: List<PathElement>) : List<PathElement> by elements {

    constructor(key: String) : this(PathElement.Key(key))
    constructor(idx: Int) : this(PathElement.Idx(idx))
    constructor(element: PathElement) : this(listOf(element))

    fun append(key: String): JsPath = append(PathElement.Key(key))
    fun append(idx: Int): JsPath = append(PathElement.Idx(idx))
    fun append(element: PathElement): JsPath = JsPath(elements + element)

    override fun toString(): String = buildString {
        append("#")
        elements.forEach { element -> append(element) }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsPath && this.elements == other.elements)

    override fun hashCode(): Int = elements.hashCode()
}
