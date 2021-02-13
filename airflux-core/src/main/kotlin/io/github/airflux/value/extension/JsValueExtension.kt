package io.github.airflux.value.extension

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue

fun JsValue.lookup(path: JsPath): JsLookup {
    val currentPath = mutableListOf<PathElement>()
    var currentValue = this
    for (pathElement in path.elements) {
        currentValue = when (pathElement) {
            is KeyPathElement -> if (currentValue is JsObject) {
                currentPath.add(pathElement)
                currentValue[pathElement.key]
                    ?: return JsLookup.Undefined.PathMissing(path = path)
            } else
                return JsLookup.Undefined.InvalidType(
                    path = JsPath(currentPath),
                    expected = JsValue.Type.OBJECT,
                    actual = currentValue.type
                )

            is IdxPathElement -> if (currentValue is JsArray<*>) {
                currentPath.add(pathElement)
                currentValue[pathElement.idx]
                    ?: return JsLookup.Undefined.PathMissing(path = path)
            } else
                return JsLookup.Undefined.InvalidType(
                    path = JsPath(currentPath),
                    expected = JsValue.Type.ARRAY,
                    actual = currentValue.type
                )
        }
    }

    return JsLookup.Defined(path = path, value = currentValue)
}
