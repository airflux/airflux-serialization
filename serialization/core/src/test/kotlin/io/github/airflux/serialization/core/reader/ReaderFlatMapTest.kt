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

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class ReaderFlatMapTest : FreeSpec() {

    companion object {
        private const val VALUE = "42"
        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
    }

    init {
        "The extension-function JsReader#bind" - {

            "when the original reader returns a successful result" - {
                val reader: JsReader<EB, Unit, String> = DummyReader.string()

                "then the new reader should return the transformed value" {
                    val source = JsString(VALUE)
                    val transformedReader =
                        reader.bind { _, result ->
                            result.value.toInt().toSuccess(result.location)
                        }
                    val result: JsReaderResult<Int> = transformedReader.read(ENV, LOCATION, source)

                    result shouldBeSuccess success(location = LOCATION, value = VALUE.toInt())
                }
            }

            "when the original reader returns an error" - {
                val failure = failure(location = LOCATION, error = JsonErrors.PathMissing)
                val reader: JsReader<EB, Unit, String> = DummyReader(result = failure)

                "then the new reader should return it error" {
                    val source = JsString(VALUE)
                    val transformedReader =
                        reader.bind { _, result ->
                            result.value.toInt().toSuccess(result.location)
                        }
                    val result = transformedReader.read(ENV, LOCATION, source)

                    result shouldBeFailure failure
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder, InvalidTypeErrorBuilder {
        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
        override fun invalidTypeError(expected: Iterable<String>, actual: String): JsReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
