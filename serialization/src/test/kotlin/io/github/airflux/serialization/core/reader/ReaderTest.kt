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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.assertAsFailure
import io.github.airflux.serialization.common.assertAsSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.ValueNode
import kotlin.test.Test

internal class ReaderTest {

    companion object {
        private val CONTEXT = ReaderContext()
        private val location = Location.empty.append("user")
        private const val ID_VALUE = "10"
        private const val IDENTIFIER_VALUE = "100"
    }

    @Test
    fun `Testing the map function of the Reader class`() {
        val reader = Reader { _, location, _ ->
            ReaderResult.Success(location = location.append("id"), value = ID_VALUE)
        }
        val transformedReader = reader.map { value -> value.toInt() }

        val result = transformedReader.read(CONTEXT, location, NullNode)

        result.assertAsSuccess(location = location.append("id"), value = ID_VALUE.toInt())
    }

    @Test
    fun `Testing the or function of the Reader class (first reader)`() {
        val idReader = Reader { _, location, _ ->
            ReaderResult.Success(location = location.append("id"), value = ID_VALUE)
        }
        val identifierReader = Reader<String> { _, location, _ ->
            ReaderResult.Failure(location = location.append("identifier"), error = JsonErrors.PathMissing)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(CONTEXT, location, NullNode)

        result.assertAsSuccess(location = location.append("id"), value = ID_VALUE)
    }

    @Test
    fun `Testing the or function of the Reader class (second reader)`() {
        val idReader = Reader<String> { _, location, _ ->
            ReaderResult.Failure(location = location.append("id"), error = JsonErrors.PathMissing)
        }
        val identifierReader = Reader { _, location, _ ->
            ReaderResult.Success(location = location.append("identifier"), value = IDENTIFIER_VALUE)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(CONTEXT, location, NullNode)

        result.assertAsSuccess(location = location.append("identifier"), value = IDENTIFIER_VALUE)
    }

    @Test
    fun `Testing the or function of the Reader class (failure both reader)`() {
        val idReader = Reader { _, location, _ ->
            ReaderResult.Failure(location = location.append("id"), error = JsonErrors.PathMissing)
        }
        val identifierReader = Reader { _, location, _ ->
            ReaderResult.Failure(
                location = location.append("identifier"),
                error = JsonErrors.InvalidType(expected = ValueNode.Type.OBJECT, actual = ValueNode.Type.STRING)
            )
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(CONTEXT, location, NullNode)

        result.assertAsFailure(
            ReaderResult.Failure.Cause(location = location.append("id"), error = JsonErrors.PathMissing),
            ReaderResult.Failure.Cause(
                location = location.append("identifier"),
                error = JsonErrors.InvalidType(expected = ValueNode.Type.OBJECT, actual = ValueNode.Type.STRING)
            )
        )
    }
}
