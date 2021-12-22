package io.github.airflux.lookup

import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsValue

@Suppress("unused")
sealed class JsLookup {

    abstract val location: JsLocation

    abstract operator fun div(name: String): JsLookup

    abstract operator fun div(idx: Int): JsLookup

    data class Defined(override val location: JsLocation, val value: JsValue) : JsLookup() {

        override fun div(name: String): JsLookup = when (value) {
            is JsObject -> value[name]
                ?.let { Defined(location = location / name, value = it) }
                ?: Undefined.PathMissing(location = location / name)

            else -> Undefined.InvalidType(location = location, expected = JsValue.Type.OBJECT, actual = value.type)
        }

        override fun div(idx: Int): JsLookup = when (value) {
            is JsArray<*> -> value[idx]
                ?.let { Defined(location = location / idx, value = it) }
                ?: Undefined.PathMissing(location = location / idx)

            else -> Undefined.InvalidType(location = location, expected = JsValue.Type.ARRAY, actual = value.type)
        }
    }

    sealed class Undefined : JsLookup() {

        override fun div(name: String): JsLookup = this

        override fun div(idx: Int): JsLookup = this

        data class PathMissing(override val location: JsLocation) : Undefined()

        data class InvalidType(override val location: JsLocation, val expected: JsValue.Type, val actual: JsValue.Type) :
            Undefined()
    }
}
