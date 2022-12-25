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

package io.github.airflux.serialization.dsl.reader.struct.property

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.dummyStringReader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.dsl.reader.struct.property.specification.defaultable
import io.github.airflux.serialization.dsl.reader.struct.property.specification.optional
import io.github.airflux.serialization.dsl.reader.struct.property.specification.required
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class StructPropertyTest : FreeSpec() {

    companion object {
        private const val DEFAULT_VALUE = "none"
        private val DEFAULT = { _: ReaderEnv<EB, Unit> -> DEFAULT_VALUE }
        private val StringReader = dummyStringReader<EB, Unit>()
    }

    init {

        "The StructProperty type" - {

            "when created an instance of the required property" - {
                val spec = required(name = "id", reader = StringReader)
                val property = StructProperty.NonNullable(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }

            "when created an instance of the requiredIf property" - {
                val spec = required(name = "id", reader = StringReader, predicate = { _, _ -> true })
                val property = StructProperty.Nullable(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }

            "when created an instance of the defaultable property" - {
                val spec = defaultable(name = "id", reader = StringReader, default = DEFAULT)
                val property = StructProperty.NonNullable(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }

            "when created an instance of the optional property" - {
                val spec = optional(name = "id", reader = StringReader)
                val property = StructProperty.Nullable(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }

            "when created an instance of the optional with default property" - {
                val spec = optional(name = "id", reader = StringReader, default = DEFAULT)
                val property = StructProperty.NonNullable(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder,
                        PathMissingErrorBuilder {

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReaderResult.Error = JsonErrors.PathMissing
    }
}
