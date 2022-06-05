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

package io.github.airflux.dsl.reader.array

import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.array.readArray
import io.github.airflux.core.value.extension.readAsArray
import io.github.airflux.dsl.reader.array.item.specification.JsArrayItemSpec
import io.github.airflux.dsl.reader.scope.JsArrayReaderConfiguration

internal class JsArrayReaderBuilder<T>(configuration: JsArrayReaderConfiguration) : JsArrayReader.Builder<T> {
    private val validation: JsArrayReader.Validation.Builder<T> = configuration.validation
        .let { JsArrayReader.Validation.Builder(before = it.before) }

    override fun validation(block: JsArrayReader.Validation.Builder<T>.() -> Unit) {
        validation.block()
    }

    override fun returns(items: JsArrayItemSpec<T>): JsArrayReader.ResultBuilder<T> =
        JsArrayReader.ResultBuilder { context, location, input ->
            readArray(context = context, location = location, from = input, items = items.reader)
        }

    override fun returns(
        prefixItems: JsArrayPrefixItemsSpec<T>,
        items: Boolean
    ): JsArrayReader.ResultBuilder<T> =
        JsArrayReader.ResultBuilder { context, location, input ->
            readArray(
                context = context,
                location = location,
                from = input,
                prefixItems = prefixItems.readers(),
                errorIfAdditionalItems = !items
            )
        }

    override fun returns(
        prefixItems: JsArrayPrefixItemsSpec<T>,
        items: JsArrayItemSpec<T>
    ): JsArrayReader.ResultBuilder<T> =
        JsArrayReader.ResultBuilder { context, location, input ->
            readArray(
                context = context,
                location = location,
                from = input,
                prefixItems = prefixItems.readers(),
                items = items.reader
            )
        }

    fun build(arrayBuilder: JsArrayReader.ResultBuilder<T>): JsArrayReader<T> =
        JsArrayReader { context, location, input ->
            input.readAsArray(context, location) { c, l, v ->
                arrayBuilder(c, l, v)
            }
        }

    companion object {

        internal fun <T> JsArrayPrefixItemsSpec<T>.readers(): List<JsReader<T>> =
            this.items.map { spec -> spec.reader }
    }
}
