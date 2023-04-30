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

public sealed class ValueNode {
    public abstract val nameOfType: String
}

public object NullNode : ValueNode() {
    override val nameOfType: String = "null"

    override fun toString(): String = "null"
}

public sealed class BooleanNode(public val get: Boolean) : ValueNode() {

    override val nameOfType: String = BooleanNode.nameOfType

    public object True : BooleanNode(true)
    public object False : BooleanNode(false)

    override fun toString(): String = get.toString()

    public companion object {
        public fun valueOf(value: Boolean): BooleanNode = if (value) True else False

        public const val nameOfType: String = "boolean"
    }
}

public class StringNode(public val get: String) : ValueNode() {

    override val nameOfType: String = StringNode.nameOfType

    override fun toString(): String = """"$get""""

    override fun equals(other: Any?): Boolean =
        this === other || (other is StringNode && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()

    public companion object {
        public const val nameOfType: String = "string"
    }
}

public sealed class NumericNode private constructor(public val get: String) : ValueNode() {

    override fun equals(other: Any?): Boolean =
        this === other || (other is NumericNode && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()

    override fun toString(): String = get

    public class Integer private constructor(value: String) : NumericNode(value) {
        override val nameOfType: String = Integer.nameOfType

        public companion object {
            public const val nameOfType: String = "integer"
            private val pattern = "^-?(0|[1-9][0-9]*)$".toRegex()

            public fun valueOrNullOf(value: String): Integer? = if (value.matches(pattern)) Integer(value) else null
        }
    }

    public class Number private constructor(value: String) : NumericNode(value) {
        override val nameOfType: String = Number.nameOfType

        public companion object {
            public const val nameOfType: String = "number"
            private val pattern = "^(-?(0|[1-9][0-9]*))((\\.[0-9]+)?|(\\.[0-9]+)?[eE][+-]?[0-9]+)$".toRegex()

            public fun valueOrNullOf(value: String): Number? = if (value.matches(pattern)) Number(value) else null
        }
    }

    public companion object
}

public fun NumericNode.Companion.valueOf(value: Byte): NumericNode =
    NumericNode.Integer.valueOrNullOf(value.toString())!!

public fun NumericNode.Companion.valueOf(value: Short): NumericNode =
    NumericNode.Integer.valueOrNullOf(value.toString())!!

public fun NumericNode.Companion.valueOf(value: Int): NumericNode =
    NumericNode.Integer.valueOrNullOf(value.toString())!!

public fun NumericNode.Companion.valueOf(value: Long): NumericNode =
    NumericNode.Integer.valueOrNullOf(value.toString())!!

public class ArrayNode(private val items: List<ValueNode> = emptyList()) : ValueNode(), Iterable<ValueNode> {

    override val nameOfType: String = ArrayNode.nameOfType

    public operator fun get(idx: JsPath.Element.Idx): ValueNode? = get(idx.get)

    public operator fun get(idx: Int): ValueNode? = items.getOrNull(idx)

    public val size: Int
        get() = items.size

    public fun isEmpty(): Boolean = items.isEmpty()

    override fun iterator(): Iterator<ValueNode> = items.iterator()

    override fun toString(): String = items.joinToString(prefix = "[", postfix = "]")

    override fun equals(other: Any?): Boolean =
        this === other || (other is ArrayNode && this.items == other.items)

    override fun hashCode(): Int = items.hashCode()

    public companion object {
        public const val nameOfType: String = "array"

        public operator fun invoke(vararg elements: ValueNode): ArrayNode = ArrayNode(elements.toList())
    }
}

public class StructNode(
    private val properties: Map<String, ValueNode> = emptyMap()
) : ValueNode(),
    Iterable<Map.Entry<String, ValueNode>> {

    override val nameOfType: String = StructNode.nameOfType

    public operator fun get(key: JsPath.Element.Key): ValueNode? = get(key.get)

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

    public companion object {
        public const val nameOfType: String = "object"

        public operator fun invoke(vararg properties: Pair<String, ValueNode>): StructNode =
            StructNode(properties.toMap())
    }
}
