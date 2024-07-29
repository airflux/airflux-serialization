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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.common.NumberMatcher
import io.github.airflux.serialization.core.path.JsPath

public sealed class JsValue {
    public abstract val type: Type

    public enum class Type {
        NULL,
        BOOLEAN,
        STRING,
        NUMBER,
        ARRAY,
        STRUCT
    }
}

public data object JsNull : JsValue() {
    override val type: Type = Type.NULL

    override fun toString(): String = "JsNull"
}

public sealed class JsBoolean(public val get: Boolean) : JsValue() {

    override val type: Type = Type.BOOLEAN

    public data object True : JsBoolean(true) {
        override fun toString(): String = "JsBoolean.True"
    }

    public data object False : JsBoolean(false) {
        override fun toString(): String = "JsBoolean.False"
    }

    public companion object {

        @JvmStatic
        public fun valueOf(value: Boolean): JsBoolean = if (value) True else False
    }
}

public class JsString(public val get: String) : JsValue() {

    override val type: Type = Type.STRING

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsString && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()

    override fun toString(): String = """JsString($get)"""

    public companion object;
}

public class JsNumber private constructor(public val get: String, public val subType: SubType) : JsValue() {

    override val type: Type = Type.NUMBER

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsNumber && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()

    override fun toString(): String = "JsNumber($get)"

    public companion object {

        public fun valueOf(value: String): JsNumber? {
            val result = NumberMatcher.match(value)
            return when (result) {
                NumberMatcher.Result.INTEGER -> JsNumber(value, SubType.INTEGER)
                NumberMatcher.Result.REAL -> JsNumber(value, SubType.REAL)
                else -> null
            }
        }
    }

    public enum class SubType {
        INTEGER,
        REAL
    }
}

public class JsArray private constructor(private val items: List<JsValue>) : JsValue(), Iterable<JsValue> {

    override val type: Type = Type.ARRAY

    public val size: Int
        get() = items.size

    public operator fun get(idx: JsPath.Element.Idx): JsValue? = get(idx.get)
    public operator fun get(idx: Int): JsValue? = items.getOrNull(idx)
    public fun isEmpty(): Boolean = items.isEmpty()

    override fun iterator(): Iterator<JsValue> = items.iterator()

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsArray && this.items == other.items)

    override fun hashCode(): Int = items.hashCode()

    override fun toString(): String = "JsArray" + items.joinToString(prefix = "(", postfix = ")")

    public class Builder internal constructor() {
        private val items = mutableListOf<JsValue>()

        public fun add(value: JsValue): Builder = apply { this@Builder.items.add(value) }
        public fun addAll(items: Iterable<JsValue>): Builder = apply { this@Builder.items.addAll(items) }
        public fun build(): JsArray = if (items.isNotEmpty()) JsArray(items) else EMPTY

        private companion object {
            private val EMPTY = JsArray(emptyList())
        }
    }

    public companion object {

        @JvmStatic
        public operator fun invoke(vararg items: JsValue): JsArray =
            builder()
                .apply { addAll(items.asIterable()) }
                .build()

        @JvmStatic
        public operator fun invoke(items: Iterable<JsValue>): JsArray =
            builder()
                .apply { addAll(items) }
                .build()

        @JvmStatic
        public fun builder(): Builder = Builder()
    }
}

@Suppress("TooManyFunctions")
public class JsStruct private constructor(private val properties: Map<String, JsValue>) : JsValue(),
                                                                                          Iterable<JsStruct.Property> {

    override val type: Type = Type.STRUCT

    public val count: Int
        get() = properties.size

    public fun isEmpty(): Boolean = properties.isEmpty()
    public operator fun contains(key: JsPath.Element.Key): Boolean = contains(key.get)
    public operator fun contains(key: String): Boolean = key in properties
    public operator fun get(key: JsPath.Element.Key): JsValue? = get(key.get)
    public operator fun get(key: String): JsValue? = properties[key]

    override fun iterator(): Iterator<Property> = PropertiesIterator(properties)

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsStruct && isEqualTo(other.properties))

    override fun hashCode(): Int = calculateHashCode()

    override fun toString(): String = "JsStruct" + properties.map { (name, value) -> "$name=$value" }
        .joinToString(prefix = "(", postfix = ")")

    public data class Property(val name: String, val value: JsValue)

    private class PropertiesIterator(target: Map<String, JsValue>) : AbstractIterator<Property>() {
        private val iterator = target.iterator()

        override fun computeNext() {
            if (iterator.hasNext())
                setNext(iterator.next()
                    .let { Property(name = it.key, value = it.value) })
            else
                done()
        }
    }

    private fun isEqualTo(other: Map<String, JsValue>): Boolean {
        if (properties.size != other.size) return false
        return properties.all { entry ->
            entry.key in other
        }
    }

    private fun calculateHashCode(): Int {
        var hash = START_HASH
        properties.forEach { (name, _) ->
            hash = SALT * hash + name.hashCode()
        }
        return hash
    }

    public class Builder internal constructor() {
        private val properties = mutableMapOf<String, JsValue>()

        public fun add(name: String, value: JsValue): Builder = apply { properties[name] = value }
        public fun addAll(items: Iterable<Pair<String, JsValue>>): Builder = apply { properties.putAll(items) }
        public operator fun contains(name: String): Boolean = name in properties
        public fun build(): JsStruct = if (properties.isNotEmpty()) JsStruct(properties) else EMPTY

        private companion object {
            private val EMPTY = JsStruct(emptyMap())
        }
    }

    public companion object {

        @JvmStatic
        public operator fun invoke(vararg properties: Pair<String, JsValue>): JsStruct =
            builder()
                .apply { addAll(properties.asIterable()) }
                .build()

        @JvmStatic
        public operator fun invoke(properties: Iterable<Pair<String, JsValue>>): JsStruct =
            builder()
                .apply { addAll(properties) }
                .build()

        @JvmStatic
        public fun builder(): Builder = Builder()

        private const val START_HASH = 7
        private const val SALT = 31
    }
}
