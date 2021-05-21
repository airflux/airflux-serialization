package io.github.airflux.dsl.reader.`object`

import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsObject

class ObjectValuesMap private constructor(private val results: Map<JsReaderProperty<*>, Any>) {

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsReaderProperty.Required<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsReaderProperty.Defaultable<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsReaderProperty.Optional<T>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsReaderProperty.OptionalWithDefault<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsReaderProperty.Nullable<T>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: JsReaderProperty.NullableWithDefault<T>): T? = results[attr]?.let { it as T }

    val isEmpty: Boolean
        get() = results.isEmpty()

    val isNotEmpty: Boolean
        get() = results.isNotEmpty()

    val size: Int
        get() = results.size

    class Builder internal constructor (){
        private val results: MutableMap<JsReaderProperty<*>, Any> = mutableMapOf()

        fun <T : Any> readValue(attr: JsReaderProperty<T>, input: JsObject): JsResult.Failure? {

            val result: JsResult<T?> = when (attr) {
                is JsReaderProperty.Required -> attr.read(input)
                is JsReaderProperty.Defaultable -> attr.read(input)
                is JsReaderProperty.Optional -> attr.read(input)
                is JsReaderProperty.OptionalWithDefault -> attr.read(input)
                is JsReaderProperty.Nullable -> attr.read(input)
                is JsReaderProperty.NullableWithDefault -> attr.read(input)
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
