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

package io.github.airflux.serialization.dsl.reader.`object`.builder.property

import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.defaultable
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.nullable
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.nullableWithDefault
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.optional
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.optionalWithDefault
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.required
import io.github.airflux.serialization.std.reader.StringReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsObjectPropertyTest : FreeSpec() {

    companion object {
        private const val DEFAULT_VALUE = "none"
    }

    init {

        "The JsObjectProperty type" - {

            "when created an instance of the required property" - {
                val spec = required(name = "id", reader = StringReader)
                val property = JsObjectProperty.Required(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }

            "when created an instance of the defaultable property" - {
                val spec = defaultable(name = "id", reader = StringReader, default = { DEFAULT_VALUE })
                val property = JsObjectProperty.Defaultable(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }

            "when created an instance of the optional property" - {
                val spec = optional(name = "id", reader = StringReader)
                val property = JsObjectProperty.Optional(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }

            "when created an instance of the optional with default property" - {
                val spec = optionalWithDefault(name = "id", reader = StringReader, default = { DEFAULT_VALUE })
                val property = JsObjectProperty.OptionalWithDefault(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }

            "when created an instance of the nullable property" - {
                val spec = nullable(name = "id", reader = StringReader)
                val property = JsObjectProperty.Nullable(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }

            "when created an instance of the nullable with default property" - {
                val spec = nullableWithDefault(name = "id", reader = StringReader, default = { DEFAULT_VALUE })
                val property = JsObjectProperty.NullableWithDefault(spec)

                "then the path should equal the path from the spec" {
                    property.path shouldBe spec.path
                }

                "then the reader should equal the reader from the spec" {
                    property.reader shouldBe spec.reader
                }
            }
        }
    }
}
