package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath

sealed interface JsReaderProperty {

    val propertyPath: JsPath.Identifiable
}
