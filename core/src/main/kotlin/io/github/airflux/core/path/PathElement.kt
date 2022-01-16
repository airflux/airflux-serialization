package io.github.airflux.core.path

sealed class PathElement {

    data class Key(val key: String) : PathElement() {
        override fun toString(): String = "/$key"
    }

    data class Idx(val idx: Int) : PathElement() {
        override fun toString(): String = "[$idx]"
    }
}
