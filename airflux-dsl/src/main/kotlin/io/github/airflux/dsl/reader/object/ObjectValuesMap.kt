package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.reader.`object`.property.DefaultableProperty
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.NullableProperty
import io.github.airflux.dsl.reader.`object`.property.NullableWithDefaultProperty
import io.github.airflux.dsl.reader.`object`.property.OptionalProperty
import io.github.airflux.dsl.reader.`object`.property.OptionalWithDefaultProperty
import io.github.airflux.dsl.reader.`object`.property.RequiredProperty
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.value.JsObject

class ObjectValuesMap private constructor(private val results: Map<JsReaderProperty, Any>) {

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: RequiredProperty<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: DefaultableProperty<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: OptionalProperty<T>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: OptionalWithDefaultProperty<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: NullableProperty<T>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: NullableWithDefaultProperty<T>): T? = results[attr]?.let { it as T }

    val isEmpty: Boolean
        get() = results.isEmpty()

    val isNotEmpty: Boolean
        get() = results.isNotEmpty()

    val size: Int
        get() = results.size

    class Builder internal constructor(
        private val context: JsReaderContext?,
        private val path: JsResultPath,
        private val input: JsObject
    ) {
        private val results: MutableMap<JsReaderProperty, Any> = mutableMapOf()

        fun tryAddValueBy(property: JsReaderProperty): JsResult.Failure? {

            val result: JsResult<Any?> = when (property) {
                is RequiredProperty<*> -> property.read(context, path, input)
                is DefaultableProperty<*> -> property.read(context, path, input)
                is OptionalProperty<*> -> property.read(context, path, input)
                is OptionalWithDefaultProperty<*> -> property.read(context, path, input)
                is NullableProperty<*> -> property.read(context, path, input)
                is NullableWithDefaultProperty<*> -> property.read(context, path, input)
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
