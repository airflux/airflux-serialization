package io.github.airflux.value.extension

import io.github.airflux.lookup.JsLookup
import io.github.airflux.lookup.extension.lookup
import io.github.airflux.path.JsPath
import io.github.airflux.value.JsValue

fun JsValue.lookup(path: JsPath): JsLookup = path.elements.fold(
    initial = JsLookup.Defined(path = JsPath.empty, value = this),
    operation = { value, pathElement ->
        when (val result = value.lookup(pathElement)) {
            is JsLookup.Defined -> result
            else -> return result
        }
    }
)
