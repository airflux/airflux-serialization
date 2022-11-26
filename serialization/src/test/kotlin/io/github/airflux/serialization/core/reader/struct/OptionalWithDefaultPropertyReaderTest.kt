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

package io.github.airflux.serialization.core.reader.struct

import io.github.airflux.serialization.common.DummyReader
import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.lookup.Lookup
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class OptionalWithDefaultPropertyReaderTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val LOCATION = Location.empty.append("name")
        private const val VALUE = "user-1"
        private const val DEFAULT_VALUE = "default-user"
        private val READER: Reader<EB, Unit, String> = DummyReader(ReaderResult.Success(value = VALUE))
        private val DEFAULT = { _: ReaderEnv<EB, Unit> -> DEFAULT_VALUE }
    }

    init {

        "The readOptional (with default) function" - {

            "when the element is defined" - {
                val lookup: Lookup = Lookup.Defined(location = LOCATION, value = StringNode(VALUE))

                "then should return the result of applying the reader" {
                    val result: ReaderResult<String?> =
                        readOptional(env = ENV, lookup = lookup, using = READER, defaultValue = DEFAULT)

                    result shouldBe ReaderResult.Success(value = VALUE)
                }
            }

            "when the element is undefined" - {
                val lookup: Lookup = Lookup.Undefined(location = LOCATION)

                "then should return the default value" {
                    val result: ReaderResult<String?> =
                        readOptional(env = ENV, lookup = lookup, using = READER, defaultValue = DEFAULT)

                    result shouldBe ReaderResult.Success(value = DEFAULT_VALUE)
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder {
        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing
    }
}
