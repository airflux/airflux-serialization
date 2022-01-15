package io.github.airflux.core.path

@Suppress("unused")
class JsPath private constructor(private val elements: List<PathElement>) : List<PathElement> by elements {

    constructor(name: String) : this(KeyPathElement(name))
    constructor(idx: Int) : this(IdxPathElement(idx))
    constructor(value: PathElement) : this(listOf(value))

    fun append(name: String): JsPath = append(KeyPathElement(name))
    fun append(idx: Int): JsPath = append(IdxPathElement(idx))
    fun append(element: PathElement): JsPath = JsPath(elements + element)

    override fun toString(): String = buildString {
        append("#")
        elements.forEach { element -> append(element) }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsPath && this.elements == other.elements)

    override fun hashCode(): Int = elements.hashCode()
}
