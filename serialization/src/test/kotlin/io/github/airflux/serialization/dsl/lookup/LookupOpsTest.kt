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

package io.github.airflux.serialization.dsl.lookup

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.lookup.LookupResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class LookupOpsTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val UNKNOWN_PROPERTY_NAME = "identifier"

        private const val ARRAY_INDEX = 0
        private const val UNKNOWN_ARRAY_INDEX = 1

        private const val VALUE = "16945018-22fb-48fd-ab06-0740b90929d6"

        private val LOCATION = Location.empty
    }

    init {

        "The LookupResult#div extension-function" - {

            "when lookup by a key element of the path" - {

                "when the receiver is of type Defined" - {
                    val defined = LookupResult.Defined(LOCATION, StructNode(ID_PROPERTY_NAME to StringNode(VALUE)))

                    "when the value contains the finding key" - {

                        "then should return the value as an instance of type Defined" {
                            val lookup = defined / ID_PROPERTY_NAME
                            lookup shouldBe LookupResult.Defined(LOCATION.append(ID_PROPERTY_NAME), StringNode(VALUE))
                        }
                    }

                    "when the value does not contain the finding key" - {

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = defined / UNKNOWN_PROPERTY_NAME
                            lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(UNKNOWN_PROPERTY_NAME))
                        }
                    }

                    "when the value does not contain a valid element type" - {

                        "then should return the value as an instance of type Undefined#InvalidType" {
                            val lookup = defined / ARRAY_INDEX
                            lookup shouldBe LookupResult.Undefined.InvalidType(
                                expected = listOf(ArrayNode.nameOfType),
                                actual = StructNode.nameOfType,
                                location = LOCATION
                            )
                        }
                    }
                }

                "when the receiver is of type Undefined#PathMissing" - {
                    val undefined = LookupResult.Undefined.PathMissing(location = LOCATION)

                    "then should return the same instance of Undefined#PathMissing type" {
                        val lookup = undefined / ID_PROPERTY_NAME
                        lookup shouldBeSameInstanceAs undefined
                    }
                }

                "when the receiver is of type Undefined#InvalidType" - {
                    val undefined = LookupResult.Undefined.InvalidType(
                        expected = listOf(StructNode.nameOfType),
                        actual = StringNode.nameOfType,
                        location = LOCATION
                    )

                    "then should return the same instance of Undefined#InvalidType type" {
                        val lookup = undefined / ID_PROPERTY_NAME
                        lookup shouldBeSameInstanceAs undefined
                    }
                }
            }

            "when lookup by an index element of the path" - {

                "when the receiver is of type Defined" - {
                    val defined = LookupResult.Defined(LOCATION, ArrayNode(StringNode(VALUE)))

                    "when the value contains the finding index" - {

                        "then should return the value as an instance of type Defined" {
                            val lookup = defined / ARRAY_INDEX
                            lookup shouldBe LookupResult.Defined(LOCATION.append(ARRAY_INDEX), StringNode(VALUE))
                        }
                    }

                    "when the value does not contain the finding index" - {

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = defined / UNKNOWN_ARRAY_INDEX
                            lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(UNKNOWN_ARRAY_INDEX))
                        }
                    }

                    "when the value does not contain a valid element type" - {

                        "then should return the value as an instance of type Undefined#InvalidType" {
                            val lookup = defined / ID_PROPERTY_NAME
                            lookup shouldBe LookupResult.Undefined.InvalidType(
                                expected = listOf(StructNode.nameOfType),
                                actual = ArrayNode.nameOfType,
                                location = LOCATION
                            )
                        }
                    }
                }

                "when the receiver is of type Undefined#PathMissing" - {
                    val undefined = LookupResult.Undefined.PathMissing(location = LOCATION)

                    "then should return the same instance of Undefined#PathMissing type" {
                        val lookup = undefined / ARRAY_INDEX
                        lookup shouldBeSameInstanceAs undefined
                    }
                }

                "when the receiver is of type Undefined#InvalidType" - {
                    val undefined = LookupResult.Undefined.InvalidType(
                        expected = listOf(ArrayNode.nameOfType),
                        actual = StructNode.nameOfType,
                        location = LOCATION
                    )

                    "then should return the same instance of Undefined#InvalidType type" {
                        val lookup = undefined / ARRAY_INDEX
                        lookup shouldBeSameInstanceAs undefined
                    }
                }
            }
        }
    }
}
