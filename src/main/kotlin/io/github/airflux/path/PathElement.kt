package io.github.airflux.path

sealed class PathElement

data class KeyPathElement(val key: String) : PathElement() {

    override fun toString(): String = "/$key"
}

data class IdxPathElement(val idx: Int) : PathElement() {

    override fun toString(): String = "[$idx]"
}
