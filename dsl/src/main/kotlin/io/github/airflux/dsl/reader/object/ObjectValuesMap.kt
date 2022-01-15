package io.github.airflux.dsl.reader.`object`

import io.github.airflux.dsl.reader.`object`.property.DefaultableProperty
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.NullableProperty
import io.github.airflux.dsl.reader.`object`.property.NullableWithDefaultProperty
import io.github.airflux.dsl.reader.`object`.property.OptionalProperty
import io.github.airflux.dsl.reader.`object`.property.OptionalWithDefaultProperty
import io.github.airflux.dsl.reader.`object`.property.RequiredProperty
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsObject

class ObjectValuesMap private constructor(private val results: Map<JsReaderProperty, Any>) {

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: RequiredProperty<T>): T = results[attr] as T
    operator fun <T : Any> RequiredProperty<T>.unaryPlus(): T = get(this)

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: DefaultableProperty<T>): T = results[attr] as T
    operator fun <T : Any> DefaultableProperty<T>.unaryPlus(): T = get(this)

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: OptionalProperty<T>): T? = results[attr]?.let { it as T }
    operator fun <T : Any> OptionalProperty<T>.unaryPlus(): T? = get(this)

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: OptionalWithDefaultProperty<T>): T = results[attr] as T
    operator fun <T : Any> OptionalWithDefaultProperty<T>.unaryPlus(): T = get(this)

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: NullableProperty<T>): T? = results[attr]?.let { it as T }
    operator fun <T : Any> NullableProperty<T>.unaryPlus(): T? = get(this)

    @Suppress("UNCHECKED_CAST")
    infix operator fun <T : Any> get(attr: NullableWithDefaultProperty<T>): T? = results[attr]?.let { it as T }
    operator fun <T : Any> NullableWithDefaultProperty<T>.unaryPlus(): T? = get(this)

    val isEmpty: Boolean
        get() = results.isEmpty()

    val isNotEmpty: Boolean
        get() = results.isNotEmpty()

    val size: Int
        get() = results.size

    class Builder internal constructor(
        private val context: JsReaderContext,
        private val location: JsLocation,
        private val input: JsObject
    ) {
        private val results: MutableMap<JsReaderProperty, Any> = mutableMapOf()

        fun tryAddValueBy(property: JsReaderProperty): JsResult.Failure? {

            val result: JsResult<Any?> = when (property) {
                is RequiredProperty<*> -> property.read(context, location, input)
                is DefaultableProperty<*> -> property.read(context, location, input)
                is OptionalProperty<*> -> property.read(context, location, input)
                is OptionalWithDefaultProperty<*> -> property.read(context, location, input)
                is NullableProperty<*> -> property.read(context, location, input)
                is NullableWithDefaultProperty<*> -> property.read(context, location, input)
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
