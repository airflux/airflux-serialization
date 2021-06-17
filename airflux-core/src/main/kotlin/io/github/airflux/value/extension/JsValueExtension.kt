package io.github.airflux.value.extension

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue

operator fun JsValue.div(name: String): JsLookup = lookup(JsResultPath.Root, KeyPathElement(name))

operator fun JsValue.div(idx: Int): JsLookup = lookup(JsResultPath.Root, IdxPathElement(idx))

fun JsValue.lookup(resultPath: JsResultPath, attributePath: JsPath.Identifiable): JsLookup =
    when (attributePath) {
        is JsPath.Identifiable.Simple -> lookup(resultPath, attributePath)
        is JsPath.Identifiable.Composite -> lookup(resultPath, attributePath)
    }

internal fun JsValue.lookup(resultPath: JsResultPath, attributePath: JsPath.Identifiable.Composite): JsLookup {
    var result: JsLookup = JsLookup.Defined(path = resultPath, value = this)
    for (pathElement in attributePath) {
        result = when (result) {
            is JsLookup.Defined -> result.value.lookup(result.path, pathElement)
            is JsLookup.Undefined -> return result
        }
    }
    return result
}

internal fun JsValue.lookup(resultPath: JsResultPath, attributePath: JsPath.Identifiable.Simple): JsLookup =
    lookup(resultPath, attributePath.value)

internal fun JsValue.lookup(resultPath: JsResultPath, pathElement: PathElement): JsLookup = when (pathElement) {
    is KeyPathElement -> lookup(resultPath, pathElement)
    is IdxPathElement -> lookup(resultPath, pathElement)
}

internal fun JsValue.lookup(resultPath: JsResultPath, pathElement: KeyPathElement): JsLookup = when (this) {
    is JsObject -> {
        val path = resultPath / pathElement
        this[pathElement.key]
            ?.let { value -> JsLookup.Defined(path = path, value = value) }
            ?: JsLookup.Undefined.PathMissing(path = path)
    }
    else -> JsLookup.Undefined.InvalidType(path = resultPath, expected = JsValue.Type.OBJECT, actual = type)
}

internal fun JsValue.lookup(resultPath: JsResultPath, pathElement: IdxPathElement): JsLookup = when (this) {
    is JsArray<*> -> {
        val path = resultPath / pathElement
        this[pathElement.idx]
            ?.let { value -> JsLookup.Defined(path = path, value = value) }
            ?: JsLookup.Undefined.PathMissing(path = path)
    }
    else -> JsLookup.Undefined.InvalidType(path = resultPath, expected = JsValue.Type.ARRAY, actual = type)
}
