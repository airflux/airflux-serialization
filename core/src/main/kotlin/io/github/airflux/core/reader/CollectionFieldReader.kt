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

package io.github.airflux.core.reader

import io.github.airflux.core.common.identity
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.fold
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsValue

/**
 * Read a node which represent as array.
 *
 * - If node does not match array type, then an error is returned that was build using [InvalidTypeErrorBuilder].
 * - If node match array type, then applies [using]
 */
fun <T : Any> readAsArray(
    context: JsReaderContext,
    location: JsLocation,
    from: JsValue,
    using: JsReader<T>,
): JsResult<List<T>> {

    fun <T> readAsArray(
        context: JsReaderContext,
        location: JsLocation,
        from: JsArray<*>,
        using: JsReader<T>,
    ): JsResult<List<T>> {

        fun <T> dispatch(acc: JsResult<MutableList<T>>, result: JsResult.Success<T>): JsResult<MutableList<T>> =
            acc.fold(
                ifFailure = ::identity,
                ifSuccess = { it.apply { value += result.value } }
            )

        fun <T> dispatch(acc: JsResult<MutableList<T>>, result: JsResult.Failure): JsResult<MutableList<T>> =
            acc.fold(
                ifFailure = { result + it },
                ifSuccess = { result }
            )

        val failFast = context.failFast
        val initial: JsResult<MutableList<T>> = JsResult.Success(location, ArrayList(from.size))
        return from.foldIndexed(initial) { idx, acc, elem ->
            val currentLocation = location.append(idx)
            using.read(context, currentLocation, elem)
                .fold(
                    ifFailure = { result ->
                        if (failFast) return result
                        dispatch(acc, result)
                    },
                    ifSuccess = { result -> dispatch(acc, result) }
                )
        }
    }

    return if (from is JsArray<*>)
        readAsArray(context, location, from, using)
    else {
        val errorBuilder = context.getValue(InvalidTypeErrorBuilder)
        JsResult.Failure(location, errorBuilder.build(JsValue.Type.ARRAY, from.type))
    }
}
