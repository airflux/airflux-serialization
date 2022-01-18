package io.github.airflux.core.path

sealed class PathElement {

    data class Key(val get: String) : PathElement() {
        override fun toString(): String = "/$get"
    }

    data class Idx(val get: Int) : PathElement() {
        override fun toString(): String = "[$get]"
    }
}
