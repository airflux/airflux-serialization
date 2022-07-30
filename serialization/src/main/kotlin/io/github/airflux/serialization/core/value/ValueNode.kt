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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.core.path.PropertyPathElement

public sealed class ValueNode {

    public enum class Type { ARRAY, BOOLEAN, NULL, NUMBER, OBJECT, STRING }

    public abstract val type: Type
}

public object NullNode : ValueNode() {
    override val type: Type = Type.NULL

    override fun toString(): String = "null"
}

public sealed class BooleanNode(public val get: Boolean) : ValueNode() {

    public companion object {
        public fun valueOf(value: Boolean): BooleanNode = if (value) True else False
    }

    override val type: Type = Type.BOOLEAN

    public object True : BooleanNode(true)
    public object False : BooleanNode(false)

    override fun toString(): String = get.toString()
}

public class StringNode(public val get: String) : ValueNode() {

    override val type: Type = Type.STRING

    override fun toString(): String = """"$get""""

    override fun equals(other: Any?): Boolean =
        this === other || (other is StringNode && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()
}

public class NumberNode private constructor(public val get: String) : ValueNode() {

    public companion object {
        private val integerNumberPattern = "^-?(0|[1-9][0-9]*)$".toRegex()
        private val realNumberPattern = "^(-?(0|[1-9][0-9]*))(\\.[0-9]+|(\\.[0-9]+)?[eE][+-]?[0-9]+)$".toRegex()
        private val pattern = "^(-?(0|[1-9][0-9]*))(\\.[0-9]+|(\\.[0-9]+)?[eE][+-]?[0-9]+)?$".toRegex()

        public fun valueOf(value: Byte): NumberNode = NumberNode(value.toString())
        public fun valueOf(value: Short): NumberNode = NumberNode(value.toString())
        public fun valueOf(value: Int): NumberNode = NumberNode(value.toString())
        public fun valueOf(value: Long): NumberNode = NumberNode(value.toString())

        public fun valueOf(value: String): NumberNode? = if (value.matches(pattern)) NumberNode(value) else null
    }

    override val type: Type = Type.NUMBER

    public val isInteger: Boolean = get.matches(integerNumberPattern)

    public val isReal: Boolean = get.matches(realNumberPattern)

    override fun toString(): String = get

    override fun equals(other: Any?): Boolean =
        this === other || (other is NumberNode && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()
}

public class ArrayNode<T : ValueNode>(private val items: List<T> = emptyList()) : ValueNode(), Iterable<T> {

    public companion object {
        public operator fun <T : ValueNode> invoke(vararg elements: T): ArrayNode<T> = ArrayNode(elements.toList())
    }

    override val type: Type = Type.ARRAY

    public operator fun get(idx: PropertyPathElement.Idx): ValueNode? = get(idx.get)

    public operator fun get(idx: Int): ValueNode? = items.getOrNull(idx)

    public val size: Int
        get() = items.size

    public fun isEmpty(): Boolean = items.isEmpty()

    override fun iterator(): Iterator<T> = items.iterator()

    override fun toString(): String = items.joinToString(prefix = "[", postfix = "]")

    override fun equals(other: Any?): Boolean =
        this === other || (other is ArrayNode<*> && this.items == other.items)

    override fun hashCode(): Int = items.hashCode()
}

public class StructNode(
    private val properties: Map<String, ValueNode> = emptyMap()
) : ValueNode(),
    Iterable<Map.Entry<String, ValueNode>> {

    public companion object {

        public operator fun invoke(vararg properties: Pair<String, ValueNode>): StructNode =
            StructNode(properties.toMap())
    }

    override val type: Type = Type.OBJECT

    public operator fun get(key: PropertyPathElement.Key): ValueNode? = get(key.get)

    public operator fun get(key: String): ValueNode? = properties[key]

    public val count: Int
        get() = properties.size

    public fun isEmpty(): Boolean = properties.isEmpty()

    override fun iterator(): Iterator<Map.Entry<String, ValueNode>> = properties.iterator()

    override fun toString(): String = properties.map { (name, value) -> """"$name": $value""" }
        .joinToString(prefix = "{", postfix = "}")

    override fun equals(other: Any?): Boolean =
        this === other || (other is StructNode && this.properties.keys == other.properties.keys)

    override fun hashCode(): Int = properties.keys.hashCode()
}
