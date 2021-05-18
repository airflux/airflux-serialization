package io.github.airflux.dsl.reader.`object`

import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsObject

class ObjectValuesMap private constructor(private val results: Map<JsProperty<*>, Any>) {

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsProperty.Required<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsProperty.Defaultable<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsProperty.Optional<T>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsProperty.OptionalWithDefault<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsProperty.Nullable<T>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsProperty.NullableWithDefault<T>): T? = results[attr]?.let { it as T }

    val isEmpty: Boolean
        get() = results.isEmpty()

    val isNotEmpty: Boolean
        get() = results.isNotEmpty()

    val size: Int
        get() = results.size

    class Builder {
        private val results: MutableMap<JsProperty<*>, Any> = mutableMapOf()

        fun <T : Any> readValue(attr: JsProperty<T>, input: JsObject): JsResult.Failure? {

            val result: JsResult<T?> = when (attr) {
                is JsProperty.Required -> attr.read(input)
                is JsProperty.Defaultable -> attr.read(input)
                is JsProperty.Optional -> attr.read(input)
                is JsProperty.OptionalWithDefault -> attr.read(input)
                is JsProperty.Nullable -> attr.read(input)
                is JsProperty.NullableWithDefault -> attr.read(input)
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

        internal fun build(): ObjectValuesMap = ObjectValuesMap(results)
    }
}
