package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.path.JsPath

sealed interface JsReaderProperty<T : Any> {

    val propertyPath: JsPath.Identifiable
}
