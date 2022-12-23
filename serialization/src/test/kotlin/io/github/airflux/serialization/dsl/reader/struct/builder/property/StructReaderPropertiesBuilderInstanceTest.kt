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
        private val NON_NULLABLE_PROPERTY_PATH = PropertyPaths(PropertyPath("required-id"))
        private val NON_NULLABLE_PROPERTY_READER =
            DummyReader<Unit, Unit, String>(ReaderResult.Success(location = LOCATION, value = ""))

        private val NULLABLE_PROPERTY_PATH = PropertyPaths(PropertyPath("optional-id"))
        private val NULLABLE_PROPERTY_READER =
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
                        property(
                            StructPropertySpec.NonNullable(
                                path = NON_NULLABLE_PROPERTY_PATH,
                                reader = NON_NULLABLE_PROPERTY_READER
                            )
                        )
                        property(
                            StructPropertySpec.Nullable(
                                path = NULLABLE_PROPERTY_PATH,
                                reader = NULLABLE_PROPERTY_READER
                            )
                        )
                    }
                    .build()

                "then the container should contain the non-nullable property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<StructProperty.NonNullable<Unit, Unit, *>>()
                        it.path shouldBe NON_NULLABLE_PROPERTY_PATH
                        it.reader shouldBe NON_NULLABLE_PROPERTY_READER
                    }
                }

                "then the container should contain the nullable property" {
                    properties.forOne {
                        it.shouldBeInstanceOf<StructProperty.Nullable<Unit, Unit, *>>()
                        it.path shouldBe NULLABLE_PROPERTY_PATH
                        it.reader shouldBe NULLABLE_PROPERTY_READER
                    }
                }
            }
        }
    }
}
