package io.github.airflux.dsl.reader.`object`

import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsObject

class ObjectValuesMap<E : JsError> private constructor(private val results: Map<Attribute<*, E>, Any>) {

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: Attribute.Required<T, E>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix fun <T : Any> by(attr: Attribute.Required<T, E>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: Attribute.Defaultable<T, E>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: Attribute.Optional<T, E>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: Attribute.OptionalWithDefault<T, E>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: Attribute.Nullable<T, E>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: Attribute.NullableWithDefault<T, E>): T? = results[attr]?.let { it as T }

    val isEmpty: Boolean
        get() = results.isEmpty()

    val isNotEmpty: Boolean
        get() = results.isNotEmpty()

    val size: Int
        get() = results.size

    class Builder<E : JsError> {
        private val results: MutableMap<Attribute<*, E>, Any> = mutableMapOf()

        fun <T : Any> readValue(attr: Attribute<T, E>, input: JsObject): JsResult.Failure<E>? {

            val result: JsResult<T?, E> = when (attr) {
                is Attribute.Required -> attr.read(input)
                is Attribute.Defaultable -> attr.read(input)
                is Attribute.Optional -> attr.read(input)
                is Attribute.OptionalWithDefault -> attr.read(input)
                is Attribute.Nullable -> attr.read(input)
                is Attribute.NullableWithDefault -> attr.read(input)
            }

            return when (result) {
                is JsResult.Success -> {
                    val value = result.value
                    if (value != null) results[attr] = value
                    null
                }
                is JsResult.Failure -> result
            }
        }

        fun build(): ObjectValuesMap<E> = ObjectValuesMap(results)
    }
}
