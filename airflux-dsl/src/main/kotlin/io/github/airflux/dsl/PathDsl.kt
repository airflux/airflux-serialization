package io.github.airflux.dsl

import io.github.airflux.path.JsPath

@Suppress("unused")
object PathDsl {
    infix operator fun String.div(child: String): JsPath = JsPath(this) + JsPath(child)

    infix operator fun String.div(idx: Int): JsPath = JsPath(this) + JsPath(idx)
}
