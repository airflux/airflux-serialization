package io.github.airflux.reader.result

import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement

sealed class JsResultPath {

    object Root : JsResultPath() {
        override fun toString(): String = "#"
    }

    class Element(val head: PathElement, val tail: JsResultPath) : JsResultPath() {

        override fun toString(): String {
            val builder = StringBuilder().append("#")
            foldRight(builder, this) { acc, value -> acc.append(value) }
            return builder.toString()
        }
    }

    operator fun div(child: String): JsResultPath = div(KeyPathElement(child))

    operator fun div(idx: Int): JsResultPath = div(IdxPathElement(idx))

    operator fun div(idx: PathElement): JsResultPath = Element(idx, this)

    override fun hashCode(): Int = foldLeft(7, this) { v, p -> v * 31 + p.hashCode() }

    override fun equals(other: Any?): Boolean {
        tailrec fun listEq(a: JsResultPath, b: JsResultPath): Boolean = when {
            a is Element && b is Element -> if (a.head == b.head) listEq(a.tail, b.tail) else false
            a is Root && b is Element -> false
            a is Element && b is Root -> false
            else -> true
        }

        return when (other) {
            is JsResultPath -> listEq(this, other)
            else -> super.equals(other)
        }
    }

    companion object {

        internal tailrec fun <R> foldLeft(initial: R, path: JsResultPath, operation: (R, PathElement) -> R): R =
            when (path) {
                is Root -> initial
                is Element -> foldLeft(operation(initial, path.head), path.tail, operation)
            }

        internal fun <R> foldRight(initial: R, path: JsResultPath, operation: (R, PathElement) -> R): R =
            when (path) {
                is Root -> initial
                is Element -> {
                    foldRight(initial, path.tail, operation)
                    operation(initial, path.head)
                }
            }
    }
}