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

package io.github.airflux.serialization.core.reader.`object`

import io.github.airflux.serialization.common.DummyReader
import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class NullablePropertyReaderTest : FreeSpec() {

    companion object {
        private val CONTEXT = ReaderContext(PathMissingErrorBuilder(builder = { JsonErrors.PathMissing }))
        private val LOCATION = Location.empty.append("name")
        private const val VALUE = "user-1"
        private val READER: Reader<String> =
            DummyReader { _, location -> ReaderResult.Success(location = location, value = VALUE) }
    }

    init {

        "The readNullable function" - {

            "when the element is defined" - {

                "when the value of the element is not the NullNode" - {
                    val from: Lookup = Lookup.Defined(location = LOCATION, value = StringNode(VALUE))

                    "then should return the result of applying the reader" {
                        val result: ReaderResult<String?> =
                            readNullable(context = CONTEXT, from = from, using = READER)
                        result shouldBe ReaderResult.Success(location = LOCATION, value = VALUE)
                    }
                }

                "when the value of the element is the NullNode" - {
                    val from: Lookup = Lookup.Defined(location = Location.empty.append("name"), NullNode)

                    "then should return the null value" {
                        val result: ReaderResult<String?> =
                            readNullable(context = CONTEXT, from = from, using = READER)
                        result shouldBe ReaderResult.Success(location = LOCATION, value = null)
                    }
                }
            }

            "when the element is undefined" - {
                val from: Lookup = Lookup.Undefined(location = LOCATION)

                "then should return the missing path error" {
                    val result: ReaderResult<String?> = readNullable(context = CONTEXT, from = from, using = READER)
                    result shouldBe ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)
                }
            }
        }
    }
}