package io.github.airflux.core.value

import io.github.airflux.core.path.IdxPathElement
import io.github.airflux.core.path.KeyPathElement

sealed class JsValue {

    enum class Type { ARRAY, BOOLEAN, NULL, NUMBER, OBJECT, STRING }

    abstract val type: Type
}

object JsNull : JsValue() {
    override val type: Type = Type.NULL

    override fun toString(): String = "null"
}

sealed class JsBoolean(val get: Boolean) : JsValue() {

    companion object {
        fun valueOf(value: Boolean): JsBoolean = if (value) True else False
    }

    override val type: Type = Type.BOOLEAN

    object True : JsBoolean(true)
    object False : JsBoolean(false)

    override fun toString(): String = get.toString()
}

class JsString(val get: String) : JsValue() {

    override val type: Type = Type.STRING

    override fun toString(): String = """"$get""""

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsString && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()
}

class JsNumber private constructor(val get: String) : JsValue() {

    companion object {
        private val integerNumberPattern = "^-?(0|[1-9][0-9]*)$".toRegex()
        private val realNumberPattern = "^(-?(0|[1-9][0-9]*))(\\.[0-9]+|(\\.[0-9]+)?[eE][+-]?[0-9]+)$".toRegex()
        private val pattern = "^(-?(0|[1-9][0-9]*))(\\.[0-9]+|(\\.[0-9]+)?[eE][+-]?[0-9]+)?$".toRegex()

        fun valueOf(value: Byte) = JsNumber(value.toString())
        fun valueOf(value: Short) = JsNumber(value.toString())
        fun valueOf(value: Int) = JsNumber(value.toString())
        fun valueOf(value: Long) = JsNumber(value.toString())

        fun valueOf(value: String): JsNumber? = if (value.matches(pattern)) JsNumber(value) else null
    }

    override val type: Type = Type.NUMBER

    val isInteger: Boolean = get.matches(integerNumberPattern)

    val isReal: Boolean = get.matches(realNumberPattern)

    override fun toString(): String = get

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsNumber && this.get == other.get)

    override fun hashCode(): Int = get.hashCode()
}

class JsArray<T : JsValue>(private val items: List<T> = emptyList()) : JsValue(), Iterable<T> {

    companion object {
        operator fun <T : JsValue> invoke(vararg elements: T): JsArray<T> = JsArray(elements.toList())
    }

    override val type: Type = Type.ARRAY

    operator fun get(path: IdxPathElement): JsValue? = get(path.idx)

    operator fun get(idx: Int): JsValue? = items.getOrNull(idx)

    val size: Int
        get() = items.size

    fun isEmpty(): Boolean = items.isEmpty()

    override fun iterator(): Iterator<T> = items.iterator()

    override fun toString(): String = items.joinToString(prefix = "[", postfix = "]")

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsArray<*> && this.items == other.items)

    override fun hashCode(): Int = items.hashCode()
}

class JsObject(private val properties: Map<String, JsValue> = emptyMap()) : JsValue(), Iterable<Map.Entry<String, JsValue>> {

    companion object {

        operator fun invoke(vararg properties: Pair<String, JsValue>): JsObject = JsObject(properties.toMap())
    }

    override val type: Type = Type.OBJECT

    operator fun get(path: KeyPathElement): JsValue? = get(path.key)

    operator fun get(name: String): JsValue? = properties[name]

    val count: Int
        get() = properties.size

    fun isEmpty(): Boolean = properties.isEmpty()

    override fun iterator(): Iterator<Map.Entry<String, JsValue>> = properties.iterator()

    override fun toString(): String = properties.map { (name, value) -> """"$name": $value""" }
        .joinToString(prefix = "{", postfix = "}")

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsObject && this.properties.keys == other.properties.keys)

    override fun hashCode(): Int = properties.keys.hashCode()
}
