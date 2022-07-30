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

package io.github.airflux.serialization.core.reader.array

import io.github.airflux.serialization.core.common.identity
import io.github.airflux.serialization.core.context.error.get
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.failFast
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.value.ArrayNode

/**
 * Read a node which represent as array.
 * @param prefixItems the reader for prefix items of an array
 * @param errorIfAdditionalItems return error if the number of items of an array is more than the number of the reader
 * for prefix items of an array
 */
public fun <T> readArray(
    context: JsReaderContext,
    location: JsLocation,
    from: ArrayNode<*>,
    prefixItems: List<JsReader<T>>,
    errorIfAdditionalItems: Boolean
): JsResult<List<T>> = from.read(
    context = context,
    location = location,
    prefixItems = prefixItems,
    items = null,
    errorIfAdditionalItems = errorIfAdditionalItems
)

/**
 * Read a node which represent as array.
 * @param items the reader for items of an array
 */
public fun <T> readArray(
    context: JsReaderContext,
    location: JsLocation,
    from: ArrayNode<*>,
    items: JsReader<T>
): JsResult<List<T>> =
    from.read(
        context = context,
        location = location,
        prefixItems = emptyList(),
        items = items,
        errorIfAdditionalItems = false
    )

/**
 * Read a node which represent as array.
 * @param prefixItems the reader for prefix items of an array
 * @param items the reader for items of an array
 */
public fun <T> readArray(
    context: JsReaderContext,
    location: JsLocation,
    from: ArrayNode<*>,
    prefixItems: List<JsReader<T>>,
    items: JsReader<T>
): JsResult<List<T>> = from.read(
    context = context,
    location = location,
    prefixItems = prefixItems,
    items = items,
    errorIfAdditionalItems = false
)

internal fun <T> ArrayNode<*>.read(
    context: JsReaderContext,
    location: JsLocation,
    prefixItems: List<JsReader<T>>,
    items: JsReader<T>?,
    errorIfAdditionalItems: Boolean
): JsResult<List<T>> {

    fun <T> getReader(idx: Int, prefixItems: List<JsReader<T>>, itemsReader: JsReader<T>?): JsReader<T>? =
        if (idx < prefixItems.size) prefixItems[idx] else itemsReader

    val failFast = context.failFast
    val errorBuilder = context[AdditionalItemsErrorBuilder]
    val initial: JsResult<MutableList<T>> = JsResult.Success(location, ArrayList(this.size))
    return this.foldIndexed(initial) { idx, acc, elem ->
        val currentLocation = location.append(idx)
        val reader: JsReader<T> = getReader(idx, prefixItems, items)
            ?: return if (errorIfAdditionalItems)
                acc + JsResult.Failure(currentLocation, errorBuilder.build())
            else
                acc

        reader.read(context, currentLocation, elem)
            .fold(
                ifFailure = { failure -> if (!failFast) acc + failure else return failure },
                ifSuccess = { success -> acc + success }
            )
    }
}

internal operator fun <T> JsResult<MutableList<T>>.plus(result: JsResult.Success<T>): JsResult<MutableList<T>> = fold(
    ifFailure = ::identity,
    ifSuccess = { success -> success.apply { value += result.value } }
)

internal operator fun <T> JsResult<MutableList<T>>.plus(result: JsResult.Failure): JsResult<MutableList<T>> = fold(
    ifFailure = { failure -> failure + result },
    ifSuccess = { result }
)
