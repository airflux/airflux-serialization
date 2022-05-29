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

import io.github.airflux.core.common.identity
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.fold
import io.github.airflux.core.value.JsObject
import io.github.airflux.dsl.reader.`object`.property.JsDefaultableReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsNullableReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsNullableWithDefaultReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsOptionalReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsOptionalWithDefaultReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.JsRequiredReaderProperty

internal class ObjectValuesMapInstance(private val results: Map<JsReaderProperty, Any>) : ObjectValuesMap {
    override val isEmpty: Boolean
        get() = results.isEmpty()

    override val isNotEmpty: Boolean
        get() = results.isNotEmpty()

    override val size: Int
        get() = results.size

    @Suppress("UNCHECKED_CAST")
    override infix operator fun <T : Any> get(attr: JsReaderProperty.Required<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    override infix operator fun <T : Any> get(attr: JsReaderProperty.Defaultable<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    override infix operator fun <T : Any> get(attr: JsReaderProperty.Optional<T>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    override infix operator fun <T : Any> get(attr: JsReaderProperty.OptionalWithDefault<T>): T = results[attr] as T

    @Suppress("UNCHECKED_CAST")
    override infix operator fun <T : Any> get(attr: JsReaderProperty.Nullable<T>): T? = results[attr]?.let { it as T }

    @Suppress("UNCHECKED_CAST")
    override infix operator fun <T : Any> get(attr: JsReaderProperty.NullableWithDefault<T>): T? =
        results[attr]?.let { it as T }

    class BuilderInstance : ObjectValuesMap.Builder {

        private val results: MutableMap<JsReaderProperty, Any> = mutableMapOf()

        override fun tryPutValueBy(
            context: JsReaderContext,
            location: JsLocation,
            property: JsReaderProperty,
            input: JsObject
        ): JsResult.Failure? {
            val result = input.read(context, location, property)
            return results.tryPutValue(property, result)
        }

        override fun build(): ObjectValuesMap = ObjectValuesMapInstance(results)

        companion object {

            internal fun JsObject.read(
                context: JsReaderContext,
                location: JsLocation,
                property: JsReaderProperty
            ): JsResult<Any?> =
                property.reader.read(context, location, this)

            internal val JsReaderProperty.reader: JsReader<Any?>
                get() = when (this) {
                    is JsRequiredReaderProperty<*> -> this.reader
                    is JsDefaultableReaderProperty<*> -> this.reader
                    is JsOptionalReaderProperty<*> -> this.reader
                    is JsOptionalWithDefaultReaderProperty<*> -> this.reader
                    is JsNullableReaderProperty<*> -> this.reader
                    is JsNullableWithDefaultReaderProperty<*> -> this.reader
                }

            internal fun MutableMap<JsReaderProperty, Any>.tryPutValue(
                property: JsReaderProperty,
                result: JsResult<Any?>
            ): JsResult.Failure? =
                result.fold(
                    ifFailure = ::identity,
                    ifSuccess = {
                        putIfNotNull(property, it.value)
                        null
                    }
                )

            internal fun MutableMap<JsReaderProperty, Any>.putIfNotNull(property: JsReaderProperty, value: Any?) {
                if (value != null) this[property] = value
            }
        }
    }
}
