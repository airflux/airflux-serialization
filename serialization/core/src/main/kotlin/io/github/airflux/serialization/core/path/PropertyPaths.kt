/*
 * Copyright 2021-2023 Maxim Sambulat.
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
public value class PropertyPaths private constructor(public val items: List<JsPath>) {

    public constructor(path: JsPath) : this(listOf(path))
    public constructor(path: JsPath, alt: JsPath) : this(listOf(path, alt))

    public fun append(path: JsPath): PropertyPaths = if (path in items) this else PropertyPaths(items + path)

    public fun append(paths: PropertyPaths): PropertyPaths {
        val appendablePaths = paths.items.filter { path -> path !in items }
        return if (appendablePaths.isEmpty()) this else PropertyPaths(items + appendablePaths)
    }

    override fun toString(): String = items.toString()
}
