package io.github.airflux.core.reader.result

import io.github.airflux.core.path.IdxPathElement
import io.github.airflux.core.path.KeyPathElement
import io.github.airflux.core.path.PathElement

sealed class JsLocation {

    object Root : JsLocation() {
        override fun toString(): String = "#"
    }

    class Element(val head: PathElement, val tail: JsLocation) : JsLocation() {

        override fun toString(): String {
            val builder = StringBuilder().append("#")
            foldRight(builder, this) { acc, value -> acc.append(value) }
            return builder.toString()
        }
    }

    operator fun div(child: String): JsLocation = div(KeyPathElement(child))

    operator fun div(idx: Int): JsLocation = div(IdxPathElement(idx))

    operator fun div(idx: PathElement): JsLocation = Element(idx, this)

    override fun hashCode(): Int = foldLeft(7, this) { v, p -> v * 31 + p.hashCode() }

    override fun equals(other: Any?): Boolean {
        tailrec fun listEq(a: JsLocation, b: JsLocation): Boolean = when {
            a is Element && b is Element -> if (a.head == b.head) listEq(a.tail, b.tail) else false
            a is Root && b is Element -> false
            a is Element && b is Root -> false
            else -> true
        }

        return when (other) {
            is JsLocation -> listEq(this, other)
            else -> super.equals(other)
        }
    }

    companion object {

        internal tailrec fun <R> foldLeft(initial: R, location: JsLocation, operation: (R, PathElement) -> R): R =
            when (location) {
                is Root -> initial
                is Element -> foldLeft(operation(initial, location.head), location.tail, operation)
            }

        internal fun <R> foldRight(initial: R, location: JsLocation, operation: (R, PathElement) -> R): R =
            when (location) {
                is Root -> initial
                is Element -> {
                    foldRight(initial, location.tail, operation)
                    operation(initial, location.head)
                }
            }
    }
}
