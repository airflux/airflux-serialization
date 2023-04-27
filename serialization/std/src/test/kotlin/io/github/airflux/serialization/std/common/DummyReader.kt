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

package io.github.airflux.serialization.std.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode

internal class DummyReader<EB, O, CTX, T> private constructor(
    val result: (ReaderEnv<EB, O>, context: CTX, Location, ValueNode) -> ReadingResult<T>
) : Reader<EB, O, CTX, T> {

    override fun read(env: ReaderEnv<EB, O>, context: CTX, location: Location, source: ValueNode): ReadingResult<T> =
        result(env, context, location, source)

    internal companion object {

        internal fun <EB, O, CTX> string(): Reader<EB, O, CTX, String>
            where EB : InvalidTypeErrorBuilder =
            DummyReader(
                result = { env, _, location, source ->
                    if (source is StringNode)
                        success(location = location, value = source.get)
                    else
                        failure(
                            location = location,
                            error = env.errorBuilders.invalidTypeError(
                                expected = listOf(StringNode.nameOfType),
                                actual = source.nameOfType
                            )
                        )
                }
            )
    }
}
