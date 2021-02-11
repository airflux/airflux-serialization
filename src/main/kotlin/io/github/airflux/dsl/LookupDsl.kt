package io.github.airflux.dsl

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue

@Suppress("unused")
object LookupDsl {

    operator fun JsValue.div(name: String): JsLookup =
        lookupAttribute(from = this, byName = name, previousPath = JsPath.empty)

    operator fun JsValue.div(idx: Int): JsLookup =
        lookupElement(from = this, byIndex = idx, previousPath = JsPath.empty)

    operator fun JsLookup.div(name: String): JsLookup = when (this) {
        is JsLookup.Defined -> lookupAttribute(from = this.value, byName = name, previousPath = path)
        is JsLookup.Undefined -> this
    }

    operator fun JsLookup.div(idx: Int): JsLookup = when (this) {
        is JsLookup.Defined -> lookupElement(from = this.value, byIndex = idx, previousPath = path)
        is JsLookup.Undefined -> this
    }

    private fun lookupAttribute(from: JsValue, byName: String, previousPath: JsPath): JsLookup = when (from) {
        is JsObject -> from[byName]
            ?.let { JsLookup.Defined(path = previousPath / byName, value = it) }
            ?: JsLookup.Undefined.PathMissing(path = previousPath / byName)

        else -> JsLookup.Undefined.InvalidType(path = previousPath, expected = JsValue.Type.OBJECT, actual = from.type)
    }

    private fun lookupElement(from: JsValue, byIndex: Int, previousPath: JsPath): JsLookup = when (from) {
        is JsArray<*> -> from[byIndex]
            ?.let { JsLookup.Defined(path = previousPath / byIndex, value = it) }
            ?: JsLookup.Undefined.PathMissing(path = previousPath / byIndex)

        else -> JsLookup.Undefined.InvalidType(path = previousPath, expected = JsValue.Type.ARRAY, actual = from.type)
    }
}
