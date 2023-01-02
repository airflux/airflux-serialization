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

package io.github.airflux.serialization.core.path

@JvmInline
public value class PropertyPaths private constructor(public val items: List<PropertyPath>) {

    public fun append(path: PropertyPath): PropertyPaths = if (path in items) this else PropertyPaths(items + path)

    public fun append(paths: PropertyPaths): PropertyPaths {
        val newPaths = paths.items.filter { path -> path !in items }
        return if (newPaths.isEmpty()) this else PropertyPaths(items + newPaths)
    }

    public inline fun <R> fold(initial: (PropertyPath) -> R, operation: (acc: R, PropertyPath) -> R): R {
        val iterator = items.iterator()
        var accumulator = initial(iterator.next())
        while (iterator.hasNext()) {
            accumulator = operation(accumulator, iterator.next())
        }
        return accumulator
    }

    override fun toString(): String = items.toString()

    public companion object {
        public operator fun invoke(path: PropertyPath): PropertyPaths = PropertyPaths(listOf(path))
        public operator fun invoke(path: PropertyPath, other: PropertyPath): PropertyPaths =
            PropertyPaths(listOf(path, other))
    }
}
