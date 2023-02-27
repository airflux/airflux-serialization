/*
 * Copyright 2021-2023 Maxim Sambulat.
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
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.value.ArrayNode

/**
 * Read a node which represent as array.
 * @param itemsReader the reader for items of an array
 */
public fun <EB, O, CTX, T> readArray(
    env: ReaderEnv<EB, O>,
    context: CTX,
    location: Location,
    source: ArrayNode,
    itemsReader: Reader<EB, O, CTX, T>
): ReaderResult<List<T>>
    where O : FailFastOption {
    val failFast = env.options.failFast
    val initial: ReaderResult<MutableList<T>> =
        ReaderResult.Success(location = location, value = ArrayList(source.size))
    return source.foldIndexed(initial) { idx, acc, elem ->
        val currentLocation = location.append(idx)
        itemsReader.read(env, context, currentLocation, elem)
            .fold(
                ifFailure = { failure -> if (!failFast) acc + failure else return failure },
                ifSuccess = { success -> acc + success }
            )
    }
}

/**
 * Read a node which represent as array.
 * @param prefixItemReaders the reader for prefix items of an array
 * @param errorIfAdditionalItems return error if the number of items of an array is more than the number of the reader
 * for prefix items of an array
 */
@Suppress("LongParameterList")
public fun <EB, O, CTX, T> readArray(
    env: ReaderEnv<EB, O>,
    context: CTX,
    location: Location,
    source: ArrayNode,
    prefixItemReaders: List<Reader<EB, O, CTX, T>>,
    errorIfAdditionalItems: Boolean
): ReaderResult<List<T>>
    where EB : AdditionalItemsErrorBuilder,
          O : FailFastOption {

    fun <EB, O, CTX, T> getReader(idx: Int, prefixItems: List<Reader<EB, O, CTX, T>>): Reader<EB, O, CTX, T>? =
        prefixItems.getOrNull(idx)

    val failFast = env.options.failFast
    val initial: ReaderResult<MutableList<T>> =
        ReaderResult.Success(location = location, value = ArrayList(source.size))
    return source.foldIndexed(initial) { idx, acc, elem ->
        val currentLocation = location.append(idx)
        val reader = getReader(idx, prefixItemReaders)
        if (reader != null) {
            reader.read(env, context, currentLocation, elem)
                .fold(
                    ifFailure = { failure -> if (!failFast) acc + failure else return failure },
                    ifSuccess = { success -> acc + success }
                )
        } else if (errorIfAdditionalItems) {
            val failure = ReaderResult.Failure(currentLocation, env.errorBuilders.additionalItemsError())
            if (failFast) return failure else acc + failure
        } else
            acc
    }
}

/**
 * Read a node which represent as array.
 * @param prefixItemReaders the reader for prefix items of an array
 * @param itemsReader the reader for items of an array
 */
@Suppress("LongParameterList")
public fun <EB, O, CTX, T> readArray(
    env: ReaderEnv<EB, O>,
    context: CTX,
    location: Location,
    source: ArrayNode,
    prefixItemReaders: List<Reader<EB, O, CTX, T>>,
    itemsReader: Reader<EB, O, CTX, T>
): ReaderResult<List<T>>
    where O : FailFastOption {

    fun <EB, O, CTX, T> getReader(
        idx: Int,
        prefixItemReaders: List<Reader<EB, O, CTX, T>>,
        itemsReader: Reader<EB, O, CTX, T>
    ): Reader<EB, O, CTX, T> =
        if (idx < prefixItemReaders.size) prefixItemReaders[idx] else itemsReader

    val failFast = env.options.failFast
    val initial: ReaderResult<MutableList<T>> =
        ReaderResult.Success(location = location, value = ArrayList(source.size))
    return source.foldIndexed(initial) { idx, acc, elem ->
        val currentLocation = location.append(idx)
        getReader(idx, prefixItemReaders, itemsReader)
            .read(env, context, currentLocation, elem)
            .fold(
                ifFailure = { failure -> if (!failFast) acc + failure else return failure },
                ifSuccess = { success -> acc + success }
            )
    }
}

internal operator fun <T> ReaderResult<MutableList<T>>.plus(
    result: ReaderResult.Success<T>
): ReaderResult<MutableList<T>> =
    fold(
        ifFailure = ::identity,
        ifSuccess = { success -> success.apply { value += result.value } }
    )

internal operator fun <T> ReaderResult<MutableList<T>>.plus(
    result: ReaderResult.Failure
): ReaderResult<MutableList<T>> =
    fold(
        ifFailure = { failure -> failure + result },
        ifSuccess = { result }
    )
