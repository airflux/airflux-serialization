package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
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

    class Builder internal constructor() {
        private val results: MutableMap<JsReaderProperty<*>, Any> = mutableMapOf()

        fun <T : Any> readValue(
            context: JsReaderContext?,
            path: JsResultPath,
            property: JsReaderProperty<T>,
            input: JsObject
        ): JsResult.Failure? {

            val result: JsResult<T?> = when (property) {
                is JsReaderProperty.Required -> property.read(context, path, input)
                is JsReaderProperty.Defaultable -> property.read(context, path, input)
                is JsReaderProperty.Optional -> property.read(context, path, input)
                is JsReaderProperty.OptionalWithDefault -> property.read(context, path, input)
                is JsReaderProperty.Nullable -> property.read(context, path, input)
                is JsReaderProperty.NullableWithDefault -> property.read(context, path, input)
            }

            return when (result) {
                is JsResult.Success -> {
                    val value = result.value
                    if (value != null) results[property] = value
                    null
                }
                is JsResult.Failure -> result
            }
        }

        internal fun build(): ObjectValuesMap = ObjectValuesMap(results)
    }
}
