/*
 * Copyright 2021-2022 Maxim Sambulat.
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

package io.github.airflux.dsl.reader.`object`

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.`object`.property.JsDefaultableReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsNullableReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsNullableWithDefaultReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsOptionalReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsOptionalWithDefaultReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsRequiredReaderProperty

interface ObjectValuesMap {

    val isEmpty: Boolean
    val isNotEmpty: Boolean
    val size: Int

    infix operator fun <T : Any> get(attr: JsReaderProperty.Required<T>): T
    operator fun <T : Any> JsReaderProperty.Required<T>.unaryPlus(): T = get(this)

    infix operator fun <T : Any> get(attr: JsReaderProperty.Defaultable<T>): T
    operator fun <T : Any> JsReaderProperty.Defaultable<T>.unaryPlus(): T = get(this)

    infix operator fun <T : Any> get(attr: JsReaderProperty.Optional<T>): T?
    operator fun <T : Any> JsReaderProperty.Optional<T>.unaryPlus(): T? = get(this)

    infix operator fun <T : Any> get(attr: JsReaderProperty.OptionalWithDefault<T>): T
    operator fun <T : Any> JsReaderProperty.OptionalWithDefault<T>.unaryPlus(): T = get(this)

    infix operator fun <T : Any> get(attr: JsReaderProperty.Nullable<T>): T?
    operator fun <T : Any> JsReaderProperty.Nullable<T>.unaryPlus(): T? = get(this)

    infix operator fun <T : Any> get(attr: JsReaderProperty.NullableWithDefault<T>): T?
    operator fun <T : Any> JsReaderProperty.NullableWithDefault<T>.unaryPlus(): T? = get(this)

    interface Builder {
        fun tryAddValueBy(property: JsReaderProperty): JsResult.Failure?
        fun build(): ObjectValuesMap
    }

    private class BuilderInstance(
        private val context: JsReaderContext,
        private val location: JsLocation,
        private val input: JsObject
    ) : Builder {

        private val results: MutableMap<JsReaderProperty, Any> = mutableMapOf()

        override fun tryAddValueBy(property: JsReaderProperty): JsResult.Failure? = when (property) {
            is JsRequiredReaderProperty<*> -> append(property, property.reader.read(context, location, input))
            is JsDefaultableReaderProperty<*> -> append(property, property.reader.read(context, location, input))
            is JsOptionalReaderProperty<*> -> append(property, property.reader.read(context, location, input))
            is JsOptionalWithDefaultReaderProperty<*> ->
                append(property, property.reader.read(context, location, input))
            is JsNullableReaderProperty<*> -> append(property, property.reader.read(context, location, input))
            is JsNullableWithDefaultReaderProperty<*> ->
                append(property, property.reader.read(context, location, input))
        }

        private fun append(property: JsReaderProperty, result: JsResult<Any?>): JsResult.Failure? = when (result) {
            is JsResult.Success -> {
                val value = result.value
                if (value != null) results[property] = value
                null
            }
            is JsResult.Failure -> result
        }

        override fun build(): ObjectValuesMap = ObjectValuesMapInstance(results)
    }

    private class ObjectValuesMapInstance(private val results: Map<JsReaderProperty, Any>) : ObjectValuesMap {

        override val isEmpty: Boolean
            get() = results.isEmpty()

        override val isNotEmpty: Boolean
            get() = results.isNotEmpty()

        override val size: Int
            get() = results.size

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> get(attr: JsReaderProperty.Required<T>): T = results[attr] as T

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> get(attr: JsReaderProperty.Defaultable<T>): T = results[attr] as T

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> get(attr: JsReaderProperty.Optional<T>): T? = results[attr]?.let { it as T }

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> get(attr: JsReaderProperty.OptionalWithDefault<T>): T = results[attr] as T

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> get(attr: JsReaderProperty.Nullable<T>): T? = results[attr]?.let { it as T }

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> get(attr: JsReaderProperty.NullableWithDefault<T>): T? = results[attr]?.let { it as T }
    }

    companion object {
        fun builder(context: JsReaderContext, location: JsLocation, input: JsObject): Builder =
            BuilderInstance(context, location, input)
    }
}
