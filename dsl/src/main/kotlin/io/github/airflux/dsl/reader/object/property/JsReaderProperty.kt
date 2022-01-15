package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.core.path.JsPath

sealed interface JsReaderProperty {

    val path: JsPath
}
