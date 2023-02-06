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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.std.common.kotest.shouldBeFailure
import io.github.airflux.serialization.std.common.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class BooleanReaderTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val BooleanReader = booleanReader<EB, Unit, Unit>()
    }

    init {

        "The boolean type reader" - {

            "should return value the true" {
                val source: ValueNode = BooleanNode.valueOf(true)
                val result = BooleanReader.read(ENV, CONTEXT, LOCATION, source)
                result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = true)
            }

            "should return value the false" {
                val source: ValueNode = BooleanNode.valueOf(false)
                val result = BooleanReader.read(ENV, CONTEXT, LOCATION, source)
                result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = false)
            }

            "should return the invalid type error" {
                val source: ValueNode = StringNode("abc")
                val result = BooleanReader.read(ENV, CONTEXT, LOCATION, source)
                result shouldBeFailure ReaderResult.Failure(
                    location = Location.empty,
                    error = JsonErrors.InvalidType(
                        expected = listOf(BooleanNode.nameOfType),
                        actual = StringNode.nameOfType
                    )
                )
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
