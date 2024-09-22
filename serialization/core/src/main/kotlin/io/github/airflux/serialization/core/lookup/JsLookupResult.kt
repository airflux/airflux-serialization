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
    public abstract val location: JsLocation

    /**
     * Example:
     * <!--- TEST_NAME JsLookupResultTest01 -->
     * <!--- INCLUDE
     * import io.github.airflux.serialization.core.location.JsLocation
     * import io.github.airflux.serialization.core.lookup.lookup
     * import io.github.airflux.serialization.core.path.JsPath
     * import io.github.airflux.serialization.core.value.JsString
     * import io.github.airflux.serialization.core.value.JsStruct
     * -->
     * ```kotlin
     * internal fun main() {
     *   val source = JsStruct(
     *      "user" to JsStruct(
     *          "phone" to JsString("123")
     *       )
     *   )
     *   val key = JsPath.Element.Key("user")
     *   val lookup = source.lookup(location = JsLocation.Root, key = key)
     *   val result = lookup.apply("phone")
     *   println(result)
     * }
     * ```
     *
     * This code prints:
     * ```text
     * Defined(location=#/user/phone, value=JsString(123))
     * ```
     * <!--- KNIT example-lookup-result-01.kt -->
     * <!--- TEST -->
     */
    public fun apply(key: String): JsLookupResult = apply(Element.Key(key))

    /**
     * Example:
     * <!--- TEST_NAME JsLookupResultTest02 -->
     * <!--- INCLUDE
     * import io.github.airflux.serialization.core.location.JsLocation
     * import io.github.airflux.serialization.core.lookup.lookup
     * import io.github.airflux.serialization.core.path.JsPath
     * import io.github.airflux.serialization.core.value.JsArray
     * import io.github.airflux.serialization.core.value.JsString
     * import io.github.airflux.serialization.core.value.JsStruct
     * -->
     * ```kotlin
     * internal fun main() {
     *   val source = JsStruct(
     *      "phones" to JsArray(
     *          JsString("123")
     *       )
     *   )
     *   val key = JsPath.Element.Key("phones")
     *   val lookup = source.lookup(location = JsLocation.Root, key = key)
     *   val result = lookup.apply(0)
     *   println(result)
     * }
     * ```
     *
     * This code prints:
     * ```text
     * Defined(location=#/phones[0], value=JsString(123))
     * ```
     * <!--- KNIT example-lookup-result-02.kt -->
     * <!--- TEST -->
     */
    public fun apply(idx: Int): JsLookupResult = apply(Element.Idx(idx))

    public abstract fun apply(key: Element.Key): JsLookupResult
    public abstract fun apply(idx: Element.Idx): JsLookupResult

    public data class Defined(
        override val location: JsLocation,
        public val value: JsValue
    ) : JsLookupResult() {
        override fun apply(key: Element.Key): JsLookupResult = value.lookup(location, key)
        override fun apply(idx: Element.Idx): JsLookupResult = value.lookup(location, idx)
    }

    public sealed class Undefined : JsLookupResult() {

        public data class PathMissing(
            override val location: JsLocation
        ) : Undefined() {
            override fun apply(key: Element.Key): JsLookupResult = PathMissing(location = this.location.append(key))
            override fun apply(idx: Element.Idx): JsLookupResult = PathMissing(location = this.location.append(idx))
        }

        public data class InvalidType(
            override val location: JsLocation,
            public val expected: JsValue.Type,
            public val actual: JsValue.Type
        ) : Undefined() {
            override fun apply(key: Element.Key): JsLookupResult = this
            override fun apply(idx: Element.Idx): JsLookupResult = this
        }
    }
}

/**
 * Lookup a value by key.
 *
 * Example:
 * <!--- TEST_NAME JsLookupTest01 -->
 * <!--- INCLUDE
 * import io.github.airflux.serialization.core.location.JsLocation
 * import io.github.airflux.serialization.core.lookup.lookup
 * import io.github.airflux.serialization.core.path.JsPath
 * import io.github.airflux.serialization.core.value.JsString
 * import io.github.airflux.serialization.core.value.JsStruct
 * -->
 * ```kotlin
 * internal fun main() {
 *   val source = JsStruct("id" to JsString("123"))
 *   val key = JsPath.Element.Key("id")
 *   val result = source.lookup(location = JsLocation.Root, key = key)
 *   println(result)
 * }
 * ```
 *
 * This code prints:
 * ```text
 * Defined(location=#/id, value=JsString(123))
 * ```
 * <!--- KNIT example-lookup-01.kt -->
 * <!--- TEST -->
 */
public fun JsValue.lookup(location: JsLocation, key: Element.Key): JsLookupResult =
    if (this is JsStruct)
        this[key]
            ?.let { JsLookupResult.Defined(location = location.append(key), value = it) }
            ?: JsLookupResult.Undefined.PathMissing(location = location.append(key))
    else
        JsLookupResult.Undefined.InvalidType(
            location = location,
            expected = JsValue.Type.STRUCT,
            actual = this.type
        )

/**
 * Lookup a value by index.
 *
 * Example:
 * <!--- TEST_NAME JsLookupTest02 -->
 * <!--- INCLUDE
 * import io.github.airflux.serialization.core.location.JsLocation
 * import io.github.airflux.serialization.core.lookup.lookup
 * import io.github.airflux.serialization.core.path.JsPath
 * import io.github.airflux.serialization.core.value.JsArray
 * import io.github.airflux.serialization.core.value.JsString
 * -->
 * ```kotlin
 * internal fun main() {
 *   val source = JsArray(JsString("1"), JsString("2"), JsString("3"))
 *   val idx = JsPath.Element.Idx(1)
 *   val result = source.lookup(location = JsLocation.Root, idx = idx)
 *   println(result)
 * }
 * ```
 *
 * This code prints:
 * ```text
 * Defined(location=#[1], value=JsString(2))
 * ```
 * <!--- KNIT example-lookup-02.kt -->
 * <!--- TEST -->
 */
public fun JsValue.lookup(location: JsLocation, idx: Element.Idx): JsLookupResult =
    if (this is JsArray)
        this[idx]
            ?.let { JsLookupResult.Defined(location = location.append(idx), value = it) }
            ?: JsLookupResult.Undefined.PathMissing(location = location.append(idx))
    else
        JsLookupResult.Undefined.InvalidType(
            location = location,
            expected = JsValue.Type.ARRAY,
            actual = this.type
        )

/**
 * Lookup a value by path.
 *
 * Example:
 * <!--- TEST_NAME JsLookupTest03 -->
 * <!--- INCLUDE
 * import io.github.airflux.serialization.core.location.JsLocation
 * import io.github.airflux.serialization.core.lookup.lookup
 * import io.github.airflux.serialization.core.path.JsPath
 * import io.github.airflux.serialization.core.value.JsArray
 * import io.github.airflux.serialization.core.value.JsString
 * import io.github.airflux.serialization.core.value.JsStruct
 * -->
 * ```kotlin
 * internal fun main() {
 *     val source = JsStruct(
 *         "user" to JsStruct(
 *             "phones" to JsArray(
 *                 JsString("123")
 *             )
 *         )
 *     )
 *     val path = JsPath("user").append("phones").append(0)
 *     val result = source.lookup(location = JsLocation.Root, path = path)
 *     println(result)
 * }
 * ```
 *
 * This code prints:
 * ```text
 * Defined(location=#/user/phones[0], value=JsString(123))
 * ```
 * <!--- KNIT example-lookup-03.kt -->
 * <!--- TEST -->
 */
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
                        location = location,
                        expected = JsValue.Type.STRUCT,
                        actual = source.type
                    )

                is Element.Idx -> if (source is JsArray) {
                    val value = source[element]
                        ?: return JsLookupResult.Undefined.PathMissing(location = location.append(path))
                    lookup(location.append(element), path.tail, value)
                } else
                    JsLookupResult.Undefined.InvalidType(
                        location = location,
                        expected = JsValue.Type.ARRAY,
                        actual = source.type
                    )
            }
        } else
            JsLookupResult.Defined(location = location, value = source)
    }

    return lookup(location, path, this)
}
