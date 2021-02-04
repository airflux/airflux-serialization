package io.github.airflux.path

import io.github.airflux.reader.result.JsError

class JsPath private constructor(val elements: List<PathElement>) {

    companion object {

        val root = JsPath(elements = emptyList())

        operator fun invoke(): JsPath = root

        operator fun invoke(key: String): JsPath = JsPath(elements = listOf(KeyPathElement(key)))

        operator fun invoke(idx: Int): JsPath = JsPath(elements = listOf(IdxPathElement(idx)))

        fun repath(errors: List<Pair<JsPath, List<JsError>>>, path: JsPath): List<Pair<JsPath, List<JsError>>> =
            errors.map { (oldPath, errors) -> path + oldPath to errors }
    }

    operator fun plus(other: JsPath): JsPath = JsPath(elements + other.elements)

    operator fun div(child: String): JsPath = JsPath(elements + KeyPathElement(child))

    operator fun div(idx: Int): JsPath = JsPath(elements + IdxPathElement(idx))

    override fun toString(): String = buildString {
        append("#")
        elements.forEach { element -> append(element) }
    }

    override fun equals(other: Any?): Boolean = if (this !== other)
        other is JsPath && this.elements == other.elements
    else
        true

    override fun hashCode(): Int = elements.hashCode()
}
