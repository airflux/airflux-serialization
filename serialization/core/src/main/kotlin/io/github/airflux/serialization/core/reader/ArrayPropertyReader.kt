/*
 * Copyright 2021-2024 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.common.identity
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.fold
import io.github.airflux.serialization.core.reader.result.plus
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsArray

/**
 * Read items of an array.
 * @param itemsReader the reader for items of an array
 */
public fun <EB, O, T> JsArray.readItems(
    env: JsReaderEnv<EB, O>,
    location: JsLocation,
    itemsReader: JsReader<EB, O, T>
): JsReaderResult<List<T>>
    where O : FailFastOption {
    val failFast = env.config.options.failFast
    val initial: JsReaderResult<MutableList<T>> = success(location = location, value = ArrayList(this.size))
    return this.foldIndexed(initial) { idx, acc, elem ->
        val currentLocation = location.append(idx)
        itemsReader.read(env, currentLocation, elem)
            .fold(
                onFailure = { failure -> if (failFast) return failure else acc.combine(failure) },
                onSuccess = { success -> acc.combine(success) }
            )
    }
}

/**
 * Read items of an array.
 * @param prefixItemReaders the reader for prefix items of an array
 * @param errorIfAdditionalItems return error if the number of items of an array is more than the number of the reader
 * for prefix items of an array
 */
public fun <EB, O, T> JsArray.readItems(
    env: JsReaderEnv<EB, O>,
    location: JsLocation,
    prefixItemReaders: List<JsReader<EB, O, T>>,
    errorIfAdditionalItems: Boolean
): JsReaderResult<List<T>>
    where EB : AdditionalItemsErrorBuilder,
          O : FailFastOption {

    fun <EB, O, T> getReader(idx: Int, prefixItems: List<JsReader<EB, O, T>>): JsReader<EB, O, T>? =
        prefixItems.getOrNull(idx)

    val failFast = env.config.options.failFast
    val initial: JsReaderResult<MutableList<T>> = success(location = location, value = ArrayList(this.size))
    return this.foldIndexed(initial) { idx, acc, elem ->
        val currentLocation = location.append(idx)
        val reader = getReader(idx, prefixItemReaders)
        if (reader != null) {
            reader.read(env, currentLocation, elem)
                .fold(
                    onFailure = { failure -> if (!failFast) acc.combine(failure) else return failure },
                    onSuccess = { success -> acc.combine(success) }
                )
        } else if (errorIfAdditionalItems) {
            val failure = JsReaderResult.Failure(currentLocation, env.config.errorBuilders.additionalItemsError())
            if (failFast) return failure else acc.combine(failure)
        } else
            acc
    }
}

/**
 * Read items of an array.
 * @param prefixItemReaders the reader for prefix items of an array
 * @param itemsReader the reader for items of an array
 */
public fun <EB, O, T> JsArray.readItems(
    env: JsReaderEnv<EB, O>,
    location: JsLocation,
    prefixItemReaders: List<JsReader<EB, O, T>>,
    itemsReader: JsReader<EB, O, T>
): JsReaderResult<List<T>>
    where O : FailFastOption {

    fun <EB, O, T> getReader(
        idx: Int,
        prefixItemReaders: List<JsReader<EB, O, T>>,
        itemsReader: JsReader<EB, O, T>
    ): JsReader<EB, O, T> =
        if (idx < prefixItemReaders.size) prefixItemReaders[idx] else itemsReader

    val failFast = env.config.options.failFast
    val initial: JsReaderResult<MutableList<T>> = success(location = location, value = ArrayList(this.size))
    return this.foldIndexed(initial) { idx, acc, elem ->
        val currentLocation = location.append(idx)
        getReader(idx, prefixItemReaders, itemsReader)
            .read(env, currentLocation, elem)
            .fold(
                onFailure = { failure -> if (failFast) return failure else acc.combine(failure) },
                onSuccess = { success -> acc.combine(success) }
            )
    }
}

private fun <T> JsReaderResult<MutableList<T>>.combine(
    result: JsReaderResult.Success<T>
): JsReaderResult<MutableList<T>> =
    fold(
        onFailure = ::identity,
        onSuccess = { success -> success.apply { value += result.value } }
    )

private fun <T> JsReaderResult<MutableList<T>>.combine(
    result: JsReaderResult.Failure
): JsReaderResult<MutableList<T>> =
    fold(
        onFailure = { failure -> failure + result },
        onSuccess = { result }
    )
