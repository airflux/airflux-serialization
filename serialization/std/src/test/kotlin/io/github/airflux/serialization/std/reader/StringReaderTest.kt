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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.std.common.kotest.shouldBeFailure
import io.github.airflux.serialization.std.common.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class StringReaderTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation
        private val StringReader = stringReader<EB, Unit, Unit>()
        private const val TEXT = "abc"
    }

    init {
        "The string type reader" - {

            "should return the string value" {
                val source: JsValue = JsString(TEXT)
                val result = StringReader.read(ENV, CONTEXT, LOCATION, source)
                result shouldBeSuccess success(location = LOCATION, value = TEXT)
            }

            "should return the invalid type error" {
                val source: JsValue = JsBoolean.valueOf(true)
                val result = StringReader.read(ENV, CONTEXT, LOCATION, source)
                result shouldBeFailure failure(
                    location = JsLocation,
                    error = JsonErrors.InvalidType(
                        expected = listOf(JsString.nameOfType),
                        actual = JsBoolean.nameOfType
                    )
                )
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}
