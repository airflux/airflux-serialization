/*
 * Copyright 2021-2024 Maxim Sambulat.
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
import io.github.airflux.serialization.core.path.JsPath.Element
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue

public sealed class JsLookupResult {

    public fun apply(key: String): JsLookupResult = apply(Element.Key(key))
    public fun apply(idx: Int): JsLookupResult = apply(Element.Idx(idx))
    public abstract fun apply(key: Element.Key): JsLookupResult
    public abstract fun apply(idx: Element.Idx): JsLookupResult

    public data class Defined(val location: JsLocation, val value: JsValue) : JsLookupResult() {
        override fun apply(key: Element.Key): JsLookupResult = value.lookup(location, key)
        override fun apply(idx: Element.Idx): JsLookupResult = value.lookup(location, idx)
    }

    public sealed class Undefined : JsLookupResult() {

        public data class PathMissing(val location: JsLocation) : Undefined() {
            override fun apply(key: Element.Key): JsLookupResult = PathMissing(location = this.location.append(key))
            override fun apply(idx: Element.Idx): JsLookupResult = PathMissing(location = this.location.append(idx))
        }

        public data class InvalidType(
            public val expected: Iterable<JsValue.Type>,
            public val actual: JsValue.Type,
            val breakpoint: JsLocation
        ) : Undefined() {
            override fun apply(key: Element.Key): JsLookupResult = this
            override fun apply(idx: Element.Idx): JsLookupResult = this
        }
    }
}

public fun JsValue.lookup(location: JsLocation, key: Element.Key): JsLookupResult =
    if (this is JsStruct)
        this[key]
            ?.let { JsLookupResult.Defined(location = location.append(key), value = it) }
            ?: JsLookupResult.Undefined.PathMissing(location = location.append(key))
    else
        JsLookupResult.Undefined.InvalidType(
            expected = listOf(JsValue.Type.STRUCT),
            actual = this.type,
            breakpoint = location
        )

public fun JsValue.lookup(location: JsLocation, idx: Element.Idx): JsLookupResult =
    if (this is JsArray)
        this[idx]
            ?.let { JsLookupResult.Defined(location = location.append(idx), value = it) }
            ?: JsLookupResult.Undefined.PathMissing(location = location.append(idx))
    else
        JsLookupResult.Undefined.InvalidType(
            expected = listOf(JsValue.Type.ARRAY),
            actual = this.type,
            breakpoint = location
        )

public fun JsValue.lookup(location: JsLocation, path: JsPath): JsLookupResult {
    tailrec fun lookup(location: JsLocation, path: JsPath?, source: JsValue): JsLookupResult {
        return if (path != null) {
            when (val element = path.head) {
                is Element.Key -> if (source is JsStruct) {
                    val value = source[element]
                        ?: return JsLookupResult.Undefined.PathMissing(location = location.append(path))
                    lookup(location.append(element), path.tail, value)
                } else
                    JsLookupResult.Undefined.InvalidType(
                        expected = listOf(JsValue.Type.STRUCT),
                        actual = source.type,
                        breakpoint = location
                    )

                is Element.Idx -> if (source is JsArray) {
                    val value = source[element]
                        ?: return JsLookupResult.Undefined.PathMissing(location = location.append(path))
                    lookup(location.append(element), path.tail, value)
                } else
                    JsLookupResult.Undefined.InvalidType(
                        expected = listOf(JsValue.Type.ARRAY),
                        actual = source.type,
                        breakpoint = location
                    )
            }
        } else
            JsLookupResult.Defined(location = location, value = source)
    }

    return lookup(location, path, this)
}
