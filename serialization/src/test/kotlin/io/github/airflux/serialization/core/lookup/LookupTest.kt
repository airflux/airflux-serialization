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

package io.github.airflux.serialization.core.lookup

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class LookupTest : FreeSpec() {

    companion object {
        private const val KEY_NAME = "id"
        private const val UNKNOWN_KEY_NAME = "identifier"
        private const val IDX = 0
        private const val UNKNOWN_IDX = 1

        private const val VALUE = "16945018-22fb-48fd-ab06-0740b90929d6"

        private val LOCATION = Location.empty
    }

    init {

        "The lookup by a key element of the path" - {

            "when the source is the StructNode type" - {

                "when the source is empty" - {
                    val value = StructNode()

                    "then should return the value as an instance of type Undefined#PathMissing" {
                        val lookup = value.lookup(LOCATION, PropertyPath.Element.Key(KEY_NAME))
                        lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(KEY_NAME))
                    }
                }

                "when the source is not empty" - {
                    val value = StructNode(KEY_NAME to StringNode(VALUE))

                    "when the source contains the finding key" - {
                        val searchKey = KEY_NAME

                        "then should return the value as an instance of type Defined" {
                            val lookup = value.lookup(LOCATION, PropertyPath.Element.Key(searchKey))
                            lookup shouldBe LookupResult.Defined(LOCATION.append(searchKey), StringNode(VALUE))
                        }
                    }

                    "when the source does not contain the finding key" - {
                        val searchKey = UNKNOWN_KEY_NAME

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = value.lookup(LOCATION, PropertyPath.Element.Key(searchKey))
                            lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(searchKey))
                        }
                    }
                }
            }

            "when the source is not the StructNode type" - {
                val value = StringNode(VALUE)

                "then should return the value as an instance of type Undefined#InvalidType" {
                    val lookup = value.lookup(LOCATION, PropertyPath.Element.Key(KEY_NAME))
                    lookup shouldBe LookupResult.Undefined.InvalidType(
                        expected = listOf(StructNode.nameOfType),
                        actual = StringNode.nameOfType,
                        location = LOCATION
                    )
                }
            }
        }

        "The lookup by an index element of the path" - {

            "when the source is the ArrayNode type" - {

                "when the source is empty" - {
                    val value = ArrayNode<StringNode>()

                    "then should return the value as an instance of type Undefined#PathMissing" {
                        val lookup = value.lookup(LOCATION, PropertyPath.Element.Idx(IDX))
                        lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(IDX))
                    }
                }

                "when the source is not empty" - {
                    val value = ArrayNode(StringNode(VALUE))

                    "when the source contains the finding index" - {
                        val searchIndex = IDX

                        "then should return the value as an instance of type Defined" {
                            val lookup = value.lookup(LOCATION, PropertyPath.Element.Idx(searchIndex))
                            lookup shouldBe LookupResult.Defined(LOCATION.append(searchIndex), StringNode(VALUE))
                        }
                    }

                    "when the source does not contain the finding index" - {
                        val searchIndex = UNKNOWN_IDX

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = value.lookup(LOCATION, PropertyPath.Element.Idx(searchIndex))
                            lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(searchIndex))
                        }
                    }
                }
            }

            "when the source is not the ArrayNode type" - {
                val value = StringNode(VALUE)

                "then should return the value as an instance of type Undefined#InvalidType" {
                    val lookup = value.lookup(LOCATION, PropertyPath.Element.Idx(IDX))
                    lookup shouldBe LookupResult.Undefined.InvalidType(
                        expected = listOf(ArrayNode.nameOfType),
                        actual = StringNode.nameOfType,
                        location = LOCATION
                    )
                }
            }
        }

        "The lookup by an path" - {

            "when the path contains a key element" - {

                "when the source is the StructNode type" - {

                    "when the source is empty" - {
                        val value = StructNode()

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = value.lookup(LOCATION, PropertyPath(KEY_NAME))
                            lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(KEY_NAME))
                        }
                    }

                    "when the source is not empty" - {
                        val value = StructNode(KEY_NAME to StringNode(VALUE))

                        "when the value contains the finding key" - {
                            val searchKey = KEY_NAME

                            "then should return the value as an instance of type Defined" {
                                val lookup = value.lookup(LOCATION, PropertyPath(searchKey))
                                lookup shouldBe LookupResult.Defined(LOCATION.append(searchKey), StringNode(VALUE))
                            }
                        }

                        "when the value does not contain the finding key" - {
                            val searchKey = UNKNOWN_KEY_NAME

                            "then should return the value as an instance of type Undefined#PathMissing" {
                                val lookup = value.lookup(LOCATION, PropertyPath(searchKey))
                                lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(searchKey))
                            }
                        }
                    }
                }

                "when the source is not the StructNode type" - {
                    val value = StringNode(VALUE)

                    "then should return the value as an instance of type Undefined#InvalidType" {
                        val lookup = value.lookup(LOCATION, PropertyPath(KEY_NAME))
                        lookup shouldBe LookupResult.Undefined.InvalidType(
                            expected = listOf(StructNode.nameOfType),
                            actual = StringNode.nameOfType,
                            location = LOCATION.append(KEY_NAME)
                        )
                    }
                }
            }

            "when the path contains a indexed element" - {

                "when the source is the ArrayNode type" - {

                    "when the source is empty" - {
                        val value = ArrayNode<StringNode>()

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = value.lookup(LOCATION, PropertyPath(IDX))
                            lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(IDX))
                        }
                    }

                    "when the source is not empty" - {
                        val value = ArrayNode(StringNode(VALUE))

                        "when the value contains the finding index" - {
                            val searchIndex = IDX

                            "then should return the value as an instance of type Defined" {
                                val lookup = value.lookup(LOCATION, PropertyPath(searchIndex))
                                lookup shouldBe LookupResult.Defined(LOCATION.append(searchIndex), StringNode(VALUE))
                            }
                        }

                        "when the value does not contain the finding index" - {
                            val searchIndex = UNKNOWN_IDX

                            "then should return the value as an instance of type Undefined#PathMissing" {
                                val lookup = value.lookup(LOCATION, PropertyPath(searchIndex))
                                lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(searchIndex))
                            }
                        }
                    }
                }

                "when the source is not the ArrayNode type" - {
                    val value = StringNode(VALUE)

                    "then should return the value as an instance of type Undefined#InvalidType" {
                        val lookup = value.lookup(LOCATION, PropertyPath(IDX))
                        lookup shouldBe LookupResult.Undefined.InvalidType(
                            expected = listOf(ArrayNode.nameOfType),
                            actual = StringNode.nameOfType,
                            location = LOCATION.append(IDX)
                        )
                    }
                }
            }
        }

        "The LookupResult#Defined" - {

            "when lookup by a key element of the path" - {

                "when the source is the StructNode type" - {

                    "when the source is empty" - {
                        val defined = LookupResult.Defined(LOCATION, StructNode())

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = defined.apply(KEY_NAME)
                            lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(KEY_NAME))
                        }
                    }

                    "when the source is not empty" - {
                        val defined = LookupResult.Defined(LOCATION, StructNode(KEY_NAME to StringNode(VALUE)))

                        "when the value contains the finding key" - {
                            val searchKey = KEY_NAME

                            "then should return the value as an instance of type Defined" {
                                val lookup = defined.apply(searchKey)
                                lookup shouldBe LookupResult.Defined(LOCATION.append(searchKey), StringNode(VALUE))
                            }
                        }

                        "when the value does not contain the finding key" - {
                            val searchKey = UNKNOWN_KEY_NAME

                            "then should return the value as an instance of type Undefined#PathMissing" {
                                val lookup = defined.apply(searchKey)
                                lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(searchKey))
                            }
                        }
                    }
                }

                "when the source is not the StructNode type" - {
                    val defined = LookupResult.Defined(LOCATION, StringNode(VALUE))

                    "then should return the value as an instance of type Undefined#InvalidType" {
                        val lookup = defined.apply(KEY_NAME)
                        lookup shouldBe LookupResult.Undefined.InvalidType(
                            expected = listOf(StructNode.nameOfType),
                            actual = StringNode.nameOfType,
                            location = LOCATION
                        )
                    }
                }
            }

            "when lookup by an index element of the path" - {

                "when the source is the ArrayNode type" - {

                    "when the source is empty" - {
                        val defined = LookupResult.Defined(LOCATION, ArrayNode<StringNode>())

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = defined.apply(IDX)
                            lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(IDX))
                        }
                    }

                    "when the source is not empty" - {
                        val defined = LookupResult.Defined(LOCATION, ArrayNode(StringNode(VALUE)))

                        "when the value contains the finding index" - {
                            val searchIndex = IDX

                            "then should return the value as an instance of type Defined" {
                                val lookup = defined.apply(searchIndex)
                                lookup shouldBe LookupResult.Defined(LOCATION.append(searchIndex), StringNode(VALUE))
                            }
                        }

                        "when the value does not contain the finding index" - {
                            val searchIndex = UNKNOWN_IDX

                            "then should return the value as an instance of type Undefine#PathMissingd" {
                                val lookup = defined.apply(searchIndex)
                                lookup shouldBe LookupResult.Undefined.PathMissing(LOCATION.append(searchIndex))
                            }
                        }
                    }
                }

                "when the source is not the ArrayNode type" - {
                    val defined = LookupResult.Defined(LOCATION, StringNode(VALUE))

                    "then should return the value as an instance of type Undefined#InvalidType" {
                        val lookup = defined.apply(IDX)
                        lookup shouldBe LookupResult.Undefined.InvalidType(
                            expected = listOf(ArrayNode.nameOfType),
                            actual = StringNode.nameOfType,
                            location = LOCATION
                        )
                    }
                }
            }
        }

        "The LookupResult#Undefined" - {
            val undefined = LookupResult.Undefined.InvalidType(
                expected = listOf(ArrayNode.nameOfType),
                actual = StringNode.nameOfType,
                location = LOCATION
            )

            "when lookup by a key element of the path" - {
                val lookup = undefined.apply(PropertyPath.Element.Key(KEY_NAME))

                "then should return the same instance of Undefined type" {
                    lookup shouldBeSameInstanceAs undefined
                }
            }

            "when lookup by an index element of the path" - {
                val lookup = undefined.apply(PropertyPath.Element.Idx(IDX))

                "then should return the same instance of Undefined type" {
                    lookup shouldBeSameInstanceAs undefined
                }
            }
        }
    }
}
