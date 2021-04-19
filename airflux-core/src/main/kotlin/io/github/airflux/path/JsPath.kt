package io.github.airflux.path

import io.github.airflux.reader.result.JsError

class JsPath internal constructor(val elements: List<PathElement>) {

    companion object {

        val empty = JsPath(elements = emptyList())

        internal operator fun invoke(key: String): JsPath = JsPath(elements = listOf(KeyPathElement(key)))

        internal operator fun invoke(idx: Int): JsPath = JsPath(elements = listOf(IdxPathElement(idx)))

        infix operator fun String.div(child: String): JsPath =
            JsPath(elements = listOf(KeyPathElement(this), KeyPathElement(child)))

        infix operator fun String.div(idx: Int): JsPath =
            JsPath(elements = listOf(KeyPathElement(this), IdxPathElement(idx)))

        fun <E : JsError> repath(errors: List<Pair<JsPath, List<E>>>, path: JsPath): List<Pair<JsPath, List<E>>> =
            errors.map { (oldPath, errors) -> path + oldPath to errors }
    }

    operator fun plus(other: JsPath): JsPath {
        return when {
            other.elements.isEmpty() -> this
            this.elements.isEmpty() -> other
            else -> JsPath(elements + other.elements)
        }
    }

    operator fun div(child: String): JsPath = JsPath(elements + KeyPathElement(child))

    operator fun div(idx: Int): JsPath = JsPath(elements + IdxPathElement(idx))

    override fun toString(): String = buildString {
        append("#")
        elements.forEach { element -> append(element) }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsPath && this.elements == other.elements)

    override fun hashCode(): Int = elements.hashCode()
}
