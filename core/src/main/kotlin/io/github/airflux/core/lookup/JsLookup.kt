package io.github.airflux.core.lookup

import io.github.airflux.core.path.IdxPathElement
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.path.KeyPathElement
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue

@Suppress("unused")
sealed class JsLookup {

    abstract val location: JsLocation

    fun apply(key: String): JsLookup = apply(KeyPathElement(key))
    fun apply(idx: Int): JsLookup = apply(IdxPathElement(idx))
    abstract fun apply(name: KeyPathElement): JsLookup
    abstract fun apply(idx: IdxPathElement): JsLookup

    data class Defined(override val location: JsLocation, val value: JsValue) : JsLookup() {
        override fun apply(name: KeyPathElement): JsLookup = apply(location, name, value)
        override fun apply(idx: IdxPathElement): JsLookup = apply(location, idx, value)
    }

    sealed class Undefined : JsLookup() {
        override fun apply(name: KeyPathElement): JsLookup = this
        override fun apply(idx: IdxPathElement): JsLookup = this

        data class PathMissing(override val location: JsLocation) : Undefined()

        data class InvalidType(
            override val location: JsLocation,
            val expected: JsValue.Type,
            val actual: JsValue.Type
        ) : Undefined()
    }

    companion object {

        fun apply(location: JsLocation, key: KeyPathElement, value: JsValue): JsLookup = when (value) {
            is JsObject -> value[key]
                ?.let { Defined(location = location / key, value = it) }
                ?: Undefined.PathMissing(location = location / key)

            else -> Undefined.InvalidType(location = location, expected = JsValue.Type.OBJECT, actual = value.type)
        }

        fun apply(location: JsLocation, idx: IdxPathElement, value: JsValue): JsLookup = when (value) {
            is JsArray<*> -> value[idx]
                ?.let { Defined(location = location / idx, value = it) }
                ?: Undefined.PathMissing(location = location / idx)

            else -> Undefined.InvalidType(location = location, expected = JsValue.Type.ARRAY, actual = value.type)
        }

        fun apply(location: JsLocation, path: JsPath, value: JsValue): JsLookup {
            tailrec fun apply(location: JsLocation, path: JsPath, posPath: Int, value: JsValue): JsLookup {
                if (posPath == path.size) return Defined(location, value)
                return when (val element = path[posPath]) {
                    is KeyPathElement -> if (value is JsObject) {
                        val currentLocation = location / element
                        val currentValue = value[element]
                            ?: return Undefined.PathMissing(location = currentLocation)
                        apply(currentLocation, path, posPath + 1, currentValue)
                    } else
                        Undefined.InvalidType(location = location, expected = JsValue.Type.OBJECT, actual = value.type)

                    is IdxPathElement -> if (value is JsArray<*>) {
                        val currentLocation = location / element
                        val currentValue = value[element]
                            ?: return Undefined.PathMissing(location = currentLocation)
                        apply(currentLocation, path, posPath + 1, currentValue)
                    } else
                        Undefined.InvalidType(location = location, expected = JsValue.Type.ARRAY, actual = value.type)
                }
            }

            return apply(location, path, 0, value)
        }
    }
}
