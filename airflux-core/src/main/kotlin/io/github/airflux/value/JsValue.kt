package io.github.airflux.value

import io.github.airflux.path.IdxPathElement
import io.github.airflux.path.KeyPathElement

sealed class JsValue {

    enum class Type { ARRAY, BOOLEAN, NULL, NUMBER, OBJECT, STRING }

    abstract val type: Type
}

object JsNull : JsValue() {
    override val type: Type = Type.NULL

    override fun toString(): String = "null"
}

sealed class JsBoolean(val underlying: Boolean) : JsValue() {

    companion object {
        fun valueOf(value: Boolean): JsBoolean = if (value) True else False
    }

    override val type: Type = Type.BOOLEAN

    object True : JsBoolean(true)
    object False : JsBoolean(false)

    override fun toString(): String = underlying.toString()
}

class JsString(val underlying: String) : JsValue() {

    override val type: Type = Type.STRING

    override fun toString(): String = """"$underlying""""

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsString && this.underlying == other.underlying)

    override fun hashCode(): Int = underlying.hashCode()
}

class JsNumber private constructor(val underlying: String) : JsValue() {

    companion object {
        private val integerNumberPattern = "^[+-]?[0-9]+\$".toRegex()
        private val realNumberPattern = "^[+-]?[0-9]+[.][0-9]+$".toRegex()
        private val pattern = "^[-]?[0-9]+([.][0-9]+)?$".toRegex()

        fun valueOf(value: Byte) = JsNumber(value.toString())
        fun valueOf(value: Short) = JsNumber(value.toString())
        fun valueOf(value: Int) = JsNumber(value.toString())
        fun valueOf(value: Long) = JsNumber(value.toString())

        fun valueOf(value: String): JsNumber? = if (value.matches(pattern)) JsNumber(value) else null
    }

    override val type: Type = Type.NUMBER

    val isInteger: Boolean = underlying.matches(integerNumberPattern)

    val isReal: Boolean = underlying.matches(realNumberPattern)

    override fun toString(): String = underlying

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsNumber && this.underlying == other.underlying)

    override fun hashCode(): Int = underlying.hashCode()
}

class JsArray<T : JsValue>(val underlying: List<T> = emptyList()) : JsValue() {

    companion object {
        operator fun <T : JsValue> invoke(vararg elements: T): JsArray<T> = JsArray(elements.toList())
    }

    override val type: Type = Type.ARRAY

    operator fun get(path: IdxPathElement): JsValue? = get(path.idx)

    operator fun get(idx: Int): JsValue? = underlying.getOrNull(idx)

    override fun toString(): String = underlying.joinToString(prefix = "[", postfix = "]")

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsArray<*> && this.underlying == other.underlying)

    override fun hashCode(): Int = underlying.hashCode()
}

class JsObject(val underlying: Map<String, JsValue> = emptyMap()) : JsValue() {

    companion object {

        operator fun invoke(vararg attributes: Pair<String, JsValue>): JsObject = JsObject(attributes.toMap())
    }

    override val type: Type = Type.OBJECT

    operator fun get(path: KeyPathElement): JsValue? = get(path.key)

    operator fun get(name: String): JsValue? = underlying[name]

    override fun toString(): String = underlying.map { (name, value) -> """"$name": $value""" }
        .joinToString(prefix = "{", postfix = "}")

    override fun equals(other: Any?): Boolean =
        this === other || (other is JsObject && this.underlying.keys == other.underlying.keys)

    override fun hashCode(): Int = underlying.keys.hashCode()
}
