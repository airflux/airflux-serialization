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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.failFast
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.value.ArrayNode

/**
 * Read a node which represent as array.
 * @param prefixItems the reader for prefix items of an array
 * @param errorIfAdditionalItems return error if the number of items of an array is more than the number of the reader
 * for prefix items of an array
 */
public fun <T> readArray(
    context: ReaderContext,
    location: Location,
    from: ArrayNode<*>,
    prefixItems: List<Reader<T>>,
    errorIfAdditionalItems: Boolean
): ReaderResult<List<T>> = from.read(
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
    context: ReaderContext,
    location: Location,
    from: ArrayNode<*>,
    items: Reader<T>
): ReaderResult<List<T>> =
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
    context: ReaderContext,
    location: Location,
    from: ArrayNode<*>,
    prefixItems: List<Reader<T>>,
    items: Reader<T>
): ReaderResult<List<T>> = from.read(
    context = context,
    location = location,
    prefixItems = prefixItems,
    items = items,
    errorIfAdditionalItems = false
)

internal fun <T> ArrayNode<*>.read(
    context: ReaderContext,
    location: Location,
    prefixItems: List<Reader<T>>,
    items: Reader<T>?,
    errorIfAdditionalItems: Boolean
): ReaderResult<List<T>> {

    fun <T> getReader(idx: Int, prefixItems: List<Reader<T>>, itemsReader: Reader<T>?): Reader<T>? =
        if (idx < prefixItems.size) prefixItems[idx] else itemsReader

    val failFast = context.failFast
    val errorBuilder = context[AdditionalItemsErrorBuilder]
    val initial: ReaderResult<MutableList<T>> = ReaderResult.Success(location, ArrayList(this.size))
    return this.foldIndexed(initial) { idx, acc, elem ->
        val currentLocation = location.append(idx)
        val reader: Reader<T> = getReader(idx, prefixItems, items)
            ?: return if (errorIfAdditionalItems)
                acc + ReaderResult.Failure(currentLocation, errorBuilder.build())
            else
                acc

        reader.read(context, currentLocation, elem)
            .fold(
                ifFailure = { failure -> if (!failFast) acc + failure else return failure },
                ifSuccess = { success -> acc + success }
            )
    }
}

internal operator fun <T> ReaderResult<MutableList<T>>.plus(result: ReaderResult.Success<T>): ReaderResult<MutableList<T>> = fold(
    ifFailure = ::identity,
    ifSuccess = { success -> success.apply { value += result.value } }
)

internal operator fun <T> ReaderResult<MutableList<T>>.plus(result: ReaderResult.Failure): ReaderResult<MutableList<T>> = fold(
    ifFailure = { failure -> failure + result },
    ifSuccess = { result }
)
