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

package io.github.airflux.serialization.dsl.reader.struct.builder.property

import io.github.airflux.serialization.common.DummyReader
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.StructPropertySpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class StructReaderPropertiesBuilderInstanceTest : FreeSpec() {

    companion object {
        private val LOCATION = Location.empty
        private val REQUIRED_PATH = PropertyPaths(PropertyPath("required-id"))
        private val REQUIRED_READER =
            DummyReader<Unit, Unit, String>(ReaderResult.Success(location = LOCATION, value = ""))

        private val DEFAULTABLE_PATH = PropertyPaths(PropertyPath("defaultable-id"))
        private val DEFAULTABLE_READER =
            DummyReader<Unit, Unit, String>(ReaderResult.Success(location = LOCATION, value = ""))

        private val OPTIONAL_PATH = PropertyPaths(PropertyPath("optional-id"))
        private val OPTIONAL_READER =
            DummyReader<Unit, Unit, String>(ReaderResult.Success(location = LOCATION, value = ""))

        private val OPTIONAL_WITH_DEFAULT_PATH = PropertyPaths(PropertyPath("optional-with-default-id"))
        private val OPTIONAL_WITH_DEFAULT_READER =
            DummyReader<Unit, Unit, String>(ReaderResult.Success(location = LOCATION, value = ""))

        private val NULLABLE_PATH = PropertyPaths(PropertyPath("nullable-id"))
        private val NULLABLE_READER =
            DummyReader<Unit, Unit, String>(ReaderResult.Success(location = LOCATION, value = ""))

        private val NULLABLE_WITH_DEFAULT_PATH = PropertyPaths(PropertyPath("nullable-with-default-id"))
        private val NULLABLE_WITH_DEFAULT_READER =
            DummyReader<Unit, Unit, String>(ReaderResult.Success(location = LOCATION, value = ""))
    }

    init {

        "The StructReaderPropertiesBuilderInstance type" - {

            "when no property is added to the builder" - {
                val properties = StructReaderPropertiesBuilderInstance<Unit, Unit>().build()

                "should be empty" {
                    properties shouldContainExactly emptyList()
                }
            }

            "when some properties were added to the builder" - {
                val properties: StructProperties<Unit, Unit> = StructReaderPropertiesBuilderInstance<Unit, Unit>()
                    .apply {
                        property(StructPropertySpec.Required(path = REQUIRED_PATH, reader = REQUIRED_READER))
                        property(StructPropertySpec.Defaultable(path = DEFAULTABLE_PATH, reader = DEFAULTABLE_READER))
                        property(StructPropertySpec.Optional(path = OPTIONAL_PATH, reader = OPTIONAL_READER))
                        property(
                            StructPropertySpec.OptionalWithDefault(
                                path = OPTIONAL_WITH_DEFAULT_PATH,
                                reader = OPTIONAL_WITH_DEFAULT_READER
                            )
                        )
                        property(StructPropertySpec.Nullable(path = NULLABLE_PATH, reader = NULLABLE_READER))
                        property(
                            StructPropertySpec.NullableWithDefault(
                                path = NULLABLE_WITH_DEFAULT_PATH,
                                reader = NULLABLE_WITH_DEFAULT_READER
                            )
                        )
                    }
                    .build()

                "then the container should contain the required property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<StructProperty.Required<Unit, Unit, *>>()
                        it.path shouldBe REQUIRED_PATH
                        it.reader shouldBe REQUIRED_READER
                    }
                }

                "then the container should contain the defaultable property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<StructProperty.Defaultable<Unit, Unit, *>>()
                        it.path shouldBe DEFAULTABLE_PATH
                        it.reader shouldBe DEFAULTABLE_READER
                    }
                }

                "then the container should contain the optional property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<StructProperty.Optional<Unit, Unit, *>>()
                        it.path shouldBe OPTIONAL_PATH
                        it.reader shouldBe OPTIONAL_READER
                    }
                }

                "then the container should contain the optional with default property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<StructProperty.OptionalWithDefault<Unit, Unit, *>>()
                        it.path shouldBe OPTIONAL_WITH_DEFAULT_PATH
                        it.reader shouldBe OPTIONAL_WITH_DEFAULT_READER
                    }
                }

                "then the container should contain the nullable property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<StructProperty.Nullable<Unit, Unit, *>>()
                        it.path shouldBe NULLABLE_PATH
                        it.reader shouldBe NULLABLE_READER
                    }
                }

                "then the container should contain the nullable with default property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<StructProperty.NullableWithDefault<Unit, Unit, *>>()
                        it.path shouldBe NULLABLE_WITH_DEFAULT_PATH
                        it.reader shouldBe NULLABLE_WITH_DEFAULT_READER
                    }
                }
            }
        }
    }
}
