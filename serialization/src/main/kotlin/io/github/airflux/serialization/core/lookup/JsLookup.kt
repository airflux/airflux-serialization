/*
 * Copyright 2021-2022 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.serialization.core.lookup

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.PathElement
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsObject
import io.github.airflux.serialization.core.value.JsValue

@Suppress("unused")
public sealed class JsLookup {

    public abstract val location: JsLocation

    public fun apply(key: String): JsLookup = apply(PathElement.Key(key))
    public fun apply(idx: Int): JsLookup = apply(PathElement.Idx(idx))
    public abstract fun apply(key: PathElement.Key): JsLookup
    public abstract fun apply(idx: PathElement.Idx): JsLookup

    public data class Defined(override val location: JsLocation, val value: JsValue) : JsLookup() {
        override fun apply(key: PathElement.Key): JsLookup = apply(location, key, value)
        override fun apply(idx: PathElement.Idx): JsLookup = apply(location, idx, value)
    }

    public sealed class Undefined : JsLookup() {
        override fun apply(key: PathElement.Key): JsLookup = this
        override fun apply(idx: PathElement.Idx): JsLookup = this

        public data class PathMissing(override val location: JsLocation) : Undefined()

        public data class InvalidType(
            override val location: JsLocation,
            val expected: JsValue.Type,
            val actual: JsValue.Type
        ) : Undefined()
    }

    public companion object {

        public fun apply(location: JsLocation, key: PathElement.Key, value: JsValue): JsLookup =
            if (value is JsObject)
                value[key]
                    ?.let { Defined(location = location.append(key), value = it) }
                    ?: Undefined.PathMissing(location = location.append(key))
            else
                Undefined.InvalidType(location = location, expected = JsValue.Type.OBJECT, actual = value.type)

        public fun apply(location: JsLocation, idx: PathElement.Idx, value: JsValue): JsLookup =
            if (value is JsArray<*>)
                value[idx]
                    ?.let { Defined(location = location.append(idx), value = it) }
                    ?: Undefined.PathMissing(location = location.append(idx))
            else
                Undefined.InvalidType(location = location, expected = JsValue.Type.ARRAY, actual = value.type)

        public fun apply(location: JsLocation, path: JsPath, value: JsValue): JsLookup {
            tailrec fun apply(location: JsLocation, path: JsPath, idxElement: Int, value: JsValue): JsLookup {
                if (idxElement == path.elements.size) return Defined(location, value)
                return when (val element = path.elements[idxElement]) {
                    is PathElement.Key -> if (value is JsObject) {
                        val currentLocation = location.append(element)
                        val currentValue = value[element]
                            ?: return Undefined.PathMissing(location = currentLocation)
                        apply(currentLocation, path, idxElement + 1, currentValue)
                    } else
                        Undefined.InvalidType(location = location, expected = JsValue.Type.OBJECT, actual = value.type)

                    is PathElement.Idx -> if (value is JsArray<*>) {
                        val currentLocation = location.append(element)
                        val currentValue = value[element]
                            ?: return Undefined.PathMissing(location = currentLocation)
                        apply(currentLocation, path, idxElement + 1, currentValue)
                    } else
                        Undefined.InvalidType(location = location, expected = JsValue.Type.ARRAY, actual = value.type)
                }
            }

            return apply(location, path, 0, value)
        }
    }
}
