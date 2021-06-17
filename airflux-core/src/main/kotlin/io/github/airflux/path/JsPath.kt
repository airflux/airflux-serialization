package io.github.airflux.path

sealed class JsPath {

    abstract operator fun div(child: String): Identifiable

    abstract operator fun div(idx: Int): Identifiable

    object Root : JsPath() {

        override operator fun div(child: String): Identifiable = Identifiable.Simple(KeyPathElement(child))

        override operator fun div(idx: Int): Identifiable = Identifiable.Simple(IdxPathElement(idx))

        override fun toString(): String = "#"
    }

    sealed class Identifiable : JsPath(), Iterable<PathElement> {

        class Simple internal constructor(val value: PathElement) : Identifiable() {

            override operator fun div(child: String): Identifiable = Composite(listOf(value, KeyPathElement(child)))

            override operator fun div(idx: Int): Identifiable = Composite(listOf(value, IdxPathElement(idx)))

            override fun toString(): String = "#$value"

            override fun equals(other: Any?): Boolean =
                this === other || (other is Simple && this.value == other.value)

            override fun hashCode(): Int = value.hashCode()

            override fun iterator(): Iterator<PathElement> = IteratorImpl()

            private inner class IteratorImpl : Iterator<PathElement> {
                private var index = 0

                override fun hasNext(): Boolean = index < 1

                override fun next(): PathElement {
                    if (!hasNext()) throw NoSuchElementException()
                    index++
                    return value
                }
            }
        }

        class Composite internal constructor(val elements: List<PathElement>) : Identifiable() {

            override operator fun div(child: String): Identifiable = Composite(elements + KeyPathElement(child))

            override operator fun div(idx: Int): Identifiable = Composite(elements + IdxPathElement(idx))

            override fun toString(): String = buildString {
                append("#")
                elements.forEach { element -> append(element) }
            }

            override fun equals(other: Any?): Boolean =
                this === other || (other is Composite && this.elements == other.elements)

            override fun hashCode(): Int = elements.hashCode()

            override fun iterator(): Iterator<PathElement> = elements.iterator()
        }

        companion object {

            infix operator fun String.div(child: String): Identifiable =
                Composite(listOf(KeyPathElement(this), KeyPathElement(child)))

            infix operator fun String.div(idx: Int): Identifiable =
                Composite(listOf(KeyPathElement(this), IdxPathElement(idx)))
        }
    }
}
