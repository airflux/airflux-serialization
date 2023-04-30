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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.core.path.JsPath

public sealed class JsValue {
    public abstract val nameOfType: String
}

public object JsNull : JsValue() {
    override val nameOfType: String = "null"

    override fun toString(): String = "null"
}

public sealed class JsBoolean(public val get: Boolean) : JsValue() {

    override val nameOfType: String = JsBoolean.nameOfType

    public object True : JsBoolean(true)
    public object False : JsBoolean(false)

    override fun toString(): String = get.toString()

    public companion object {
        public fun valueOf(value: Boolean): JsBoolean = if (value) True else False

        public const val nameOfType: String = "boolean"
    }
}

public class JsString(public val get: String) : JsValue() {

    override val nameOfType: String = JsString.nameOfType

    override fun toString(): String = """"$get""""

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsString && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()

    public companion object {
        public const val nameOfType: String = "string"
    }
}

public sealed class JsNumeric private constructor(public val get: String) : JsValue() {

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsNumeric && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()

    override fun toString(): String = get

    public class Integer private constructor(value: String) : JsNumeric(value) {
        override val nameOfType: String = Integer.nameOfType

        public companion object {
            public const val nameOfType: String = "integer"
            private val pattern = "^-?(0|[1-9][0-9]*)$".toRegex()

            public fun valueOrNullOf(value: String): Integer? = if (value.matches(pattern)) Integer(value) else null
        }
    }

    public class Number private constructor(value: String) : JsNumeric(value) {
        override val nameOfType: String = Number.nameOfType

        public companion object {
            public const val nameOfType: String = "number"
            private val pattern = "^(-?(0|[1-9][0-9]*))((\\.[0-9]+)?|(\\.[0-9]+)?[eE][+-]?[0-9]+)$".toRegex()

            public fun valueOrNullOf(value: String): Number? = if (value.matches(pattern)) Number(value) else null
        }
    }

    public companion object
}

public fun JsNumeric.Companion.valueOf(value: Byte): JsNumeric =
    JsNumeric.Integer.valueOrNullOf(value.toString())!!

public fun JsNumeric.Companion.valueOf(value: Short): JsNumeric =
    JsNumeric.Integer.valueOrNullOf(value.toString())!!

public fun JsNumeric.Companion.valueOf(value: Int): JsNumeric =
    JsNumeric.Integer.valueOrNullOf(value.toString())!!

public fun JsNumeric.Companion.valueOf(value: Long): JsNumeric =
    JsNumeric.Integer.valueOrNullOf(value.toString())!!

public class JsArray(private val items: List<JsValue> = emptyList()) : JsValue(), Iterable<JsValue> {

    override val nameOfType: String = JsArray.nameOfType

    public operator fun get(idx: JsPath.Element.Idx): JsValue? = get(idx.get)

    public operator fun get(idx: Int): JsValue? = items.getOrNull(idx)

    public val size: Int
        get() = items.size

    public fun isEmpty(): Boolean = items.isEmpty()

    override fun iterator(): Iterator<JsValue> = items.iterator()

    override fun toString(): String = items.joinToString(prefix = "[", postfix = "]")

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsArray && this.items == other.items)

    override fun hashCode(): Int = items.hashCode()

    public companion object {
        public const val nameOfType: String = "array"

        public operator fun invoke(vararg elements: JsValue): JsArray = JsArray(elements.toList())
    }
}

public class JsStruct(
    private val properties: Map<String, JsValue> = emptyMap()
) : JsValue(),
    Iterable<Map.Entry<String, JsValue>> {

    override val nameOfType: String = JsStruct.nameOfType

    public operator fun get(key: JsPath.Element.Key): JsValue? = get(key.get)

    public operator fun get(key: String): JsValue? = properties[key]

    public val count: Int
        get() = properties.size

    public fun isEmpty(): Boolean = properties.isEmpty()

    override fun iterator(): Iterator<Map.Entry<String, JsValue>> = properties.iterator()

    override fun toString(): String = properties.map { (name, value) -> """"$name": $value""" }
        .joinToString(prefix = "{", postfix = "}")

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsStruct && this.properties.keys == other.properties.keys)

    override fun hashCode(): Int = properties.keys.hashCode()

    public companion object {
        public const val nameOfType: String = "object"

        public operator fun invoke(vararg properties: Pair<String, JsValue>): JsStruct =
            JsStruct(properties.toMap())
    }
}
