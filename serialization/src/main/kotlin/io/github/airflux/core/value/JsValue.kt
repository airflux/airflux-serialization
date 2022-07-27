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

package io.github.airflux.core.value

import io.github.airflux.core.path.PathElement

public sealed class JsValue {

    public enum class Type { ARRAY, BOOLEAN, NULL, NUMBER, OBJECT, STRING }

    public abstract val type: Type
}

public object JsNull : JsValue() {
    override val type: Type = Type.NULL

    override fun toString(): String = "null"
}

public sealed class JsBoolean(public val get: Boolean) : JsValue() {

    public companion object {
        public fun valueOf(value: Boolean): JsBoolean = if (value) True else False
    }

    override val type: Type = Type.BOOLEAN

    public object True : JsBoolean(true)
    public object False : JsBoolean(false)

    override fun toString(): String = get.toString()
}

public class JsString(public val get: String) : JsValue() {

    override val type: Type = Type.STRING

    override fun toString(): String = """"$get""""

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsString && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()
}

public class JsNumber private constructor(public val get: String) : JsValue() {

    public companion object {
        private val integerNumberPattern = "^-?(0|[1-9][0-9]*)$".toRegex()
        private val realNumberPattern = "^(-?(0|[1-9][0-9]*))(\\.[0-9]+|(\\.[0-9]+)?[eE][+-]?[0-9]+)$".toRegex()
        private val pattern = "^(-?(0|[1-9][0-9]*))(\\.[0-9]+|(\\.[0-9]+)?[eE][+-]?[0-9]+)?$".toRegex()

        public fun valueOf(value: Byte): JsNumber = JsNumber(value.toString())
        public fun valueOf(value: Short): JsNumber = JsNumber(value.toString())
        public fun valueOf(value: Int): JsNumber = JsNumber(value.toString())
        public fun valueOf(value: Long): JsNumber = JsNumber(value.toString())

        public fun valueOf(value: String): JsNumber? = if (value.matches(pattern)) JsNumber(value) else null
    }

    override val type: Type = Type.NUMBER

    public val isInteger: Boolean = get.matches(integerNumberPattern)

    public val isReal: Boolean = get.matches(realNumberPattern)

    override fun toString(): String = get

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsNumber && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()
}

public class JsArray<T : JsValue>(private val items: List<T> = emptyList()) : JsValue(), Iterable<T> {

    public companion object {
        public operator fun <T : JsValue> invoke(vararg elements: T): JsArray<T> = JsArray(elements.toList())
    }

    override val type: Type = Type.ARRAY

    public operator fun get(idx: PathElement.Idx): JsValue? = get(idx.get)

    public operator fun get(idx: Int): JsValue? = items.getOrNull(idx)

    public val size: Int
        get() = items.size

    public fun isEmpty(): Boolean = items.isEmpty()

    override fun iterator(): Iterator<T> = items.iterator()

    override fun toString(): String = items.joinToString(prefix = "[", postfix = "]")

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsArray<*> && this.items == other.items)

    override fun hashCode(): Int = items.hashCode()
}

public class JsObject(private val properties: Map<String, JsValue> = emptyMap()) : JsValue(),
                                                                                   Iterable<Map.Entry<String, JsValue>> {

    public companion object {

        public operator fun invoke(vararg properties: Pair<String, JsValue>): JsObject = JsObject(properties.toMap())
    }

    override val type: Type = Type.OBJECT

    public operator fun get(key: PathElement.Key): JsValue? = get(key.get)

    public operator fun get(key: String): JsValue? = properties[key]

    public val count: Int
        get() = properties.size

    public fun isEmpty(): Boolean = properties.isEmpty()

    override fun iterator(): Iterator<Map.Entry<String, JsValue>> = properties.iterator()

    override fun toString(): String = properties.map { (name, value) -> """"$name": $value""" }
        .joinToString(prefix = "{", postfix = "}")

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsObject && this.properties.keys == other.properties.keys)

    override fun hashCode(): Int = properties.keys.hashCode()
}
