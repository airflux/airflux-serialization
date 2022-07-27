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

import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.dsl.reader.`object`.builder.property.specification.JsObjectPropertySpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class JsObjectReaderPropertiesBuilderInstanceTest : FreeSpec() {

    companion object {
        private val REQUIRED_PATH = JsPaths(JsPath("required-id"))
        private val REQUIRED_READER = JsReader { _, location, _ -> JsResult.Success(location, "") }

        private val DEFAULTABLE_PATH = JsPaths(JsPath("defaultable-id"))
        private val DEFAULTABLE_READER = JsReader { _, location, _ -> JsResult.Success(location, "") }

        private val OPTIONAL_PATH = JsPaths(JsPath("optional-id"))
        private val OPTIONAL_READER = JsReader { _, location, _ -> JsResult.Success(location, "") }

        private val OPTIONAL_WITH_DEFAULT_PATH = JsPaths(JsPath("optional-with-default-id"))
        private val OPTIONAL_WITH_DEFAULT_READER = JsReader { _, location, _ -> JsResult.Success(location, "") }

        private val NULLABLE_PATH = JsPaths(JsPath("nullable-id"))
        private val NULLABLE_READER = JsReader { _, location, _ -> JsResult.Success(location, "") }

        private val NULLABLE_WITH_DEFAULT_PATH = JsPaths(JsPath("nullable-with-default-id"))
        private val NULLABLE_WITH_DEFAULT_READER = JsReader { _, location, _ -> JsResult.Success(location, "") }
    }

    init {

        "The JsObjectReaderPropertiesBuilderInstance type" - {

            "when no property is added to the builder" - {
                val properties = JsObjectReaderPropertiesBuilderInstance().build()

                "should be empty" {
                    properties shouldContainExactly emptyList()
                }
            }

            "when some properties were added to the builder" - {
                val properties: JsObjectProperties = JsObjectReaderPropertiesBuilderInstance()
                    .apply {
                        property(JsObjectPropertySpec.Required(path = REQUIRED_PATH, reader = REQUIRED_READER))
                        property(JsObjectPropertySpec.Defaultable(path = DEFAULTABLE_PATH, reader = DEFAULTABLE_READER))
                        property(JsObjectPropertySpec.Optional(path = OPTIONAL_PATH, reader = OPTIONAL_READER))
                        property(
                            JsObjectPropertySpec.OptionalWithDefault(
                                path = OPTIONAL_WITH_DEFAULT_PATH,
                                reader = OPTIONAL_WITH_DEFAULT_READER
                            )
                        )
                        property(JsObjectPropertySpec.Nullable(path = NULLABLE_PATH, reader = NULLABLE_READER))
                        property(
                            JsObjectPropertySpec.NullableWithDefault(
                                path = NULLABLE_WITH_DEFAULT_PATH,
                                reader = NULLABLE_WITH_DEFAULT_READER
                            )
                        )
                    }
                    .build()

                "then the container should contain the required property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<JsObjectProperty.Required<*>>()
                        it.path shouldBe REQUIRED_PATH
                        it.reader shouldBe REQUIRED_READER
                    }
                }

                "then the container should contain the defaultable property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<JsObjectProperty.Defaultable<*>>()
                        it.path shouldBe DEFAULTABLE_PATH
                        it.reader shouldBe DEFAULTABLE_READER
                    }
                }

                "then the container should contain the optional property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<JsObjectProperty.Optional<*>>()
                        it.path shouldBe OPTIONAL_PATH
                        it.reader shouldBe OPTIONAL_READER
                    }
                }

                "then the container should contain the optional with default property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<JsObjectProperty.OptionalWithDefault<*>>()
                        it.path shouldBe OPTIONAL_WITH_DEFAULT_PATH
                        it.reader shouldBe OPTIONAL_WITH_DEFAULT_READER
                    }
                }

                "then the container should contain the nullable property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<JsObjectProperty.Nullable<*>>()
                        it.path shouldBe NULLABLE_PATH
                        it.reader shouldBe NULLABLE_READER
                    }
                }

                "then the container should contain the nullable with default property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<JsObjectProperty.NullableWithDefault<*>>()
                        it.path shouldBe NULLABLE_WITH_DEFAULT_PATH
                        it.reader shouldBe NULLABLE_WITH_DEFAULT_READER
                    }
                }
            }
        }
    }
}
