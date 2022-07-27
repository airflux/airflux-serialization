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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsValue
import kotlin.test.Test

internal class JsReaderTest {

    companion object {
        private val CONTEXT = JsReaderContext()
        private val location = JsLocation.empty.append("user")
        private const val ID_VALUE = "10"
        private const val IDENTIFIER_VALUE = "100"
    }

    @Test
    fun `Testing the map function of the JsReader class`() {
        val reader = JsReader { _, location, _ ->
            JsResult.Success(location = location.append("id"), value = ID_VALUE)
        }
        val transformedReader = reader.map { value -> value.toInt() }

        val result = transformedReader.read(CONTEXT, location, JsNull)

        result.assertAsSuccess(location = location.append("id"), value = ID_VALUE.toInt())
    }

    @Test
    fun `Testing the or function of the JsReader class (first reader)`() {
        val idReader = JsReader { _, location, _ ->
            JsResult.Success(location = location.append("id"), value = ID_VALUE)
        }
        val identifierReader = JsReader<String> { _, location, _ ->
            JsResult.Failure(location = location.append("identifier"), error = JsonErrors.PathMissing)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(CONTEXT, location, JsNull)

        result.assertAsSuccess(location = location.append("id"), value = ID_VALUE)
    }

    @Test
    fun `Testing the or function of the JsReader class (second reader)`() {
        val idReader = JsReader<String> { _, location, _ ->
            JsResult.Failure(location = location.append("id"), error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, location, _ ->
            JsResult.Success(location = location.append("identifier"), value = IDENTIFIER_VALUE)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(CONTEXT, location, JsNull)

        result.assertAsSuccess(location = location.append("identifier"), value = IDENTIFIER_VALUE)
    }

    @Test
    fun `Testing the or function of the JsReader class (failure both reader)`() {
        val idReader = JsReader { _, location, _ ->
            JsResult.Failure(location = location.append("id"), error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, location, _ ->
            JsResult.Failure(
                location = location.append("identifier"),
                error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(CONTEXT, location, JsNull)

        result.assertAsFailure(
            JsResult.Failure.Cause(location = location.append("id"), error = JsonErrors.PathMissing),
            JsResult.Failure.Cause(
                location = location.append("identifier"),
                error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        )
    }
}
