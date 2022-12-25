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

package io.github.airflux.serialization.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode

internal class DummyReader<EB, CTX, T>(
    val result: (ReaderEnv<EB, CTX>, Location, ValueNode) -> ReaderResult<T>
) : Reader<EB, CTX, T> {

    constructor(result: ReaderResult<T>) : this({ _, _, _ -> result })

    override fun read(env: ReaderEnv<EB, CTX>, location: Location, source: ValueNode): ReaderResult<T> =
        result(env, location, source)
}

internal fun <EB, CTX> dummyBooleanReader(): Reader<EB, CTX, Boolean>
    where EB : InvalidTypeErrorBuilder =
    DummyReader(
        result = { env, location, source ->
            if (source is BooleanNode)
                ReaderResult.Success(location = location, value = source.get)
            else
                ReaderResult.Failure(
                    location = location,
                    error = env.errorBuilders.invalidTypeError(
                        expected = listOf(BooleanNode.nameOfType),
                        actual = source.nameOfType
                    )
                )
        }
    )

internal fun <EB, CTX> dummyStringReader(): Reader<EB, CTX, String>
    where EB : InvalidTypeErrorBuilder =
    DummyReader(
        result = { env, location, source ->
            if (source is StringNode)
                ReaderResult.Success(location = location, value = source.get)
            else
                ReaderResult.Failure(
                    location = location,
                    error = env.errorBuilders.invalidTypeError(
                        expected = listOf(StringNode.nameOfType),
                        actual = source.nameOfType
                    )
                )
        }
    )

internal fun <EB, CTX> dummyIntReader(): Reader<EB, CTX, Int>
    where EB : InvalidTypeErrorBuilder =
    DummyReader(
        result = { env, location, source ->
            if (source is NumericNode.Integer)
                ReaderResult.Success(location = location, value = source.get.toInt())
            else
                ReaderResult.Failure(
                    location = location,
                    error = env.errorBuilders.invalidTypeError(
                        expected = listOf(NumericNode.Integer.nameOfType),
                        actual = source.nameOfType
                    )
                )
        }
    )

internal fun <EB, CTX> dummyLongReader(): Reader<EB, CTX, Long>
    where EB : InvalidTypeErrorBuilder =
    DummyReader(
        result = { env, location, source ->
            if (source is NumericNode)
                ReaderResult.Success(location = location, value = source.get.toLong())
            else
                ReaderResult.Failure(
                    location = location,
                    error = env.errorBuilders.invalidTypeError(
                        expected = listOf(NumericNode.Integer.nameOfType, NumericNode.Number.nameOfType),
                        actual = source.nameOfType
                    )
                )
        }
    )
