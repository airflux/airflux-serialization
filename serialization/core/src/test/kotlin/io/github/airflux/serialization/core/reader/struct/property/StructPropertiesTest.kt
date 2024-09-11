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

package io.github.airflux.serialization.core.reader.struct.property

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.readRequired
import io.github.airflux.serialization.core.reader.result.JsReaderResult
import io.github.airflux.serialization.core.reader.struct.property.specification.StructPropertySpec
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.test.dummy.DummyReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class StructPropertiesTest : FreeSpec() {

    init {

        "The StructProperties type" - {

            "the extension function `startKeysOfPaths`" - {

                "when the properties are empty" - {

                    val properties: StructProperties<EB, OPTS> = emptyList()

                    "then this function should return an empty set" {
                        val keys = properties.startKeysOfPaths()
                        keys shouldContainExactly emptySet()
                    }
                }

                "when properties do not contain paths with start elements being keys" - {

                    val properties: StructProperties<EB, OPTS> = listOf(

                        // [0]
                        property(path = JsPath(FIRST_ARRAY_ELEMENT_INDEX)),

                        // [1]
                        property(path = JsPath(SECOND_ARRAY_ELEMENT_INDEX))
                    )

                    "then this function should return an empty set" {
                        val keys = properties.startKeysOfPaths()
                        keys shouldContainExactly emptySet()
                    }
                }

                "when properties contain paths with start elements being keys" - {

                    "then this function should return the set of the keys" {
                        val properties: StructProperties<EB, OPTS> = listOf(

                            // id
                            property(path = JsPath(ID_PROPERTY_NAME)),

                            // [0]
                            property(path = JsPath(FIRST_ARRAY_ELEMENT_INDEX)),

                            // phones[0].mobile
                            property(
                                path = JsPath(PHONES_PROPERTY_NAME)
                                    .append(FIRST_ARRAY_ELEMENT_INDEX)
                                    .append(MOBILE_PHONE_PROPERTY_NAME)
                            ),

                            // address.city
                            property(path = JsPath(ADDRESS_PROPERTY_NAME).append(ADDRESS_CITY_PROPERTY_NAME))
                        )

                        val keys = properties.startKeysOfPaths()

                        keys shouldContainExactly setOf(
                            ID_PROPERTY_NAME,
                            PHONES_PROPERTY_NAME,
                            ADDRESS_PROPERTY_NAME
                        )
                    }
                }
            }
        }
    }

    private fun property(path: JsPath): StructProperty<EB, OPTS, String> = StructProperty(
        spec = StructPropertySpec(
            paths = JsPaths(path),
            reader = path.readRequired(StringReader)
        )
    )

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val PHONES_PROPERTY_NAME = "phones"
        private const val ADDRESS_PROPERTY_NAME = "address"
        private const val ADDRESS_CITY_PROPERTY_NAME = "city"
        private const val MOBILE_PHONE_PROPERTY_NAME = "mobile"
        private const val FIRST_ARRAY_ELEMENT_INDEX = 0
        private const val SECOND_ARRAY_ELEMENT_INDEX = 1

        private val StringReader: JsReader<EB, OPTS, String> = DummyReader.string()
    }

    private class EB : InvalidTypeErrorBuilder,
                       PathMissingErrorBuilder {
        override fun invalidTypeError(expected: JsValue.Type, actual: JsValue.Type): JsReaderResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): JsReaderResult.Error = JsonErrors.PathMissing
    }

    private class OPTS(override val failFast: Boolean) : FailFastOption
}
