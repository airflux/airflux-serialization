/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.core.path

@Suppress("unused")
class JsPath private constructor(private val elements: List<PathElement>) : List<PathElement> by elements {

    constructor(key: String) : this(PathElement.Key(key))
    constructor(idx: Int) : this(PathElement.Idx(idx))
    constructor(element: PathElement) : this(listOf(element))

    fun append(key: String): JsPath = append(PathElement.Key(key))
    fun append(idx: Int): JsPath = append(PathElement.Idx(idx))
    fun append(element: PathElement): JsPath = JsPath(elements + element)

    override fun toString(): String = buildString {
        append("#")
        elements.forEach { element -> append(element) }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsPath && this.elements == other.elements)

    override fun hashCode(): Int = elements.hashCode()
}
