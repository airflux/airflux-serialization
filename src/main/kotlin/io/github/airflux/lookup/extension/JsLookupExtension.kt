package io.github.airflux.lookup.extension

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue

internal fun JsLookup.Defined.lookup(path: PathElement): JsLookup = when (path) {
    is KeyPathElement -> this.lookup(path)
    is IdxPathElement -> this.lookup(path)
}

internal fun JsLookup.Defined.lookup(path: KeyPathElement): JsLookup = when (value) {
    is JsObject -> value[path.key]
        ?.let { JsLookup.Defined(path = this.path / path, value = it) }
        ?: JsLookup.Undefined.PathMissing(path = this.path / path)
    else -> JsLookup.Undefined.InvalidType(path = this.path, expected = JsValue.Type.OBJECT, actual = value.type)
}

internal fun JsLookup.Defined.lookup(path: IdxPathElement): JsLookup = when (value) {
    is JsArray<*> -> value[path.idx]
        ?.let { JsLookup.Defined(path = this.path / path, value = it) }
        ?: JsLookup.Undefined.PathMissing(path = this.path / path)
    else -> JsLookup.Undefined.InvalidType(path = this.path, expected = JsValue.Type.ARRAY, actual = value.type)
}
