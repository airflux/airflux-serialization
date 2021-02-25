package io.github.airflux.path.extension

import io.github.airflux.path.JsPath

infix operator fun String.div(child: String): JsPath = JsPath(this) + JsPath(child)

infix operator fun String.div(idx: Int): JsPath = JsPath(this) + JsPath(idx)
