package io.github.airflux.value.extension

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue

fun JsValue.seek(path: JsPath): JsResult<JsValue> = get(path)
    ?.let { value -> JsResult.Success(value) }
    ?: JsResult.Failure(error = JsError.PathMissing)

fun JsValue.lookup(path: JsPath): JsLookup = get(path)
    ?.let { value -> JsLookup.Defined(path = JsPath(), value = value) }
    ?: JsLookup.Undefined.PathMissing(path = JsPath())

operator fun JsValue.get(path: JsPath): JsValue? {
    return path.elements.fold(
        initial = this,
        operation = { value, pathElement ->
            value[pathElement] ?: return null
        }
    )
}

internal operator fun JsValue.get(path: PathElement): JsValue? = when (path) {
    is KeyPathElement -> this.get(path)
    is IdxPathElement -> this.get(path)
}

internal fun JsValue.get(path: KeyPathElement): JsValue? = when (this) {
    is JsObject -> this[path.key]
    else -> null
}

internal fun JsValue.get(path: IdxPathElement): JsValue? = when (this) {
    is JsArray<*> -> this.get(path.idx)
    else -> null
}
