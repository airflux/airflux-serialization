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

package io.github.airflux.dsl.reader.`object`.property.path

import io.github.airflux.core.path.JsPath

@Suppress("unused")
class JsPaths private constructor(val items: List<JsPath>) {

    constructor(path: JsPath, vararg other: JsPath) : this(path, other.asList())
    constructor(path: JsPath, other: List<JsPath>) : this(listOf(path) + other)

    fun append(path: JsPath): JsPaths = JsPaths(items + path)
    fun append(paths: JsPaths): JsPaths = JsPaths(items + paths.items)

    inline fun <R> fold(initial: (JsPath) -> R, operation: (acc: R, JsPath) -> R): R {
        val iterator = items.iterator()
        var accumulator = initial(iterator.next())
        while (iterator.hasNext()) {
            accumulator = operation(accumulator, iterator.next())
        }
        return accumulator
    }

    override fun toString(): String = items.toString()

    companion object
}
