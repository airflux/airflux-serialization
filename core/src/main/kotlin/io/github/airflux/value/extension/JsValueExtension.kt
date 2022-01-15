package io.github.airflux.value.extension

import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.JsPath
import io.github.airflux.path.KeyPathElement
import io.github.airflux.path.PathElement
import io.github.airflux.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue

operator fun JsValue.div(name: String): JsLookup = lookup(JsLocation.Root, KeyPathElement(name))

operator fun JsValue.div(idx: Int): JsLookup = lookup(JsLocation.Root, IdxPathElement(idx))

fun JsValue.lookup(location: JsLocation, path: JsPath): JsLookup {
    var result: JsLookup = JsLookup.Defined(location = location, value = this)
    for (pathElement in path) {
        result = when (result) {
            is JsLookup.Defined -> result.value.lookup(result.location, pathElement)
            is JsLookup.Undefined -> return result
        }
    }
    return result
}

internal fun JsValue.lookup(location: JsLocation, pathElement: PathElement): JsLookup = when (pathElement) {
    is KeyPathElement -> JsLookup.apply(location, pathElement, this)
    is IdxPathElement -> JsLookup.apply(location, pathElement, this)
}

fun JsValue.readAsBoolean(location: JsLocation, invalidTypeErrorBuilder: InvalidTypeErrorBuilder) =
    when (this) {
        is JsBoolean -> JsResult.Success(this.get, location = location)
        else ->
            JsResult.Failure(
                location = location,
                error = invalidTypeErrorBuilder.build(JsValue.Type.BOOLEAN, this.type)
            )
    }

fun JsValue.readAsString(location: JsLocation, invalidTypeErrorBuilder: InvalidTypeErrorBuilder) =
    when (this) {
        is JsString -> JsResult.Success(this.get, location = location)
        else ->
            JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.STRING, this.type))
    }

fun <T : Number> JsValue.readAsNumber(
    location: JsLocation,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    reader: (JsLocation, String) -> JsResult<T>
) =
    when (this) {
        is JsNumber -> reader(location, this.get)
        else ->
            JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.NUMBER, this.type))
    }

fun <T> JsValue.readAsObject(
    location: JsLocation,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    reader: (JsLocation, JsObject) -> JsResult<T>
) =
    when (this) {
        is JsObject -> reader(location, this)
        else ->
            JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.OBJECT, this.type))
    }
