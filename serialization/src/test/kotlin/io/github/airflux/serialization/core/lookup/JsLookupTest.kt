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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.PathElement
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsObject
import io.github.airflux.serialization.core.value.JsString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class JsLookupTest : FreeSpec() {

    companion object {
        private const val KEY_NAME = "id"
        private val KEY_ELEMENT_PATH = PathElement.Key(KEY_NAME)

        private const val UNKNOWN_KEY_NAME = "identifier"
        private val UNKNOWN_KEY_ELEMENT_PATH = PathElement.Key(UNKNOWN_KEY_NAME)

        private const val IDX = 0
        private val IDX_ELEMENT_PATH = PathElement.Idx(IDX)

        private const val VALUE = "16945018-22fb-48fd-ab06-0740b90929d6"

        private val LOCATION = JsLocation.empty
    }

    init {

        "The lookup by a key element of the path" - {

            "when the value contains the finding key" - {
                val value = JsObject(KEY_NAME to JsString(VALUE))

                "then should return the value as an instance of type Defined" {
                    val lookup = value.lookup(LOCATION, KEY_ELEMENT_PATH)
                    lookup shouldBe JsLookup.Defined(LOCATION.append(KEY_NAME), JsString(VALUE))
                }
            }

            "when the value does not contain the finding key" - {
                val value = JsObject(KEY_NAME to JsString(VALUE))

                "then should return the value as an instance of type Undefined" {
                    val lookup = value.lookup(LOCATION, UNKNOWN_KEY_ELEMENT_PATH)
                    lookup shouldBe JsLookup.Undefined(LOCATION.append(UNKNOWN_KEY_NAME))
                }
            }

            "when a value is an invalid type" - {
                val value = JsString(VALUE)

                "then should return the value as an instance of type Undefined" {
                    val lookup = value.lookup(LOCATION, KEY_ELEMENT_PATH)
                    lookup shouldBe JsLookup.Undefined(LOCATION.append(KEY_NAME))
                }
            }
        }

        "The lookup by an index element of the path" - {

            "when the value contains the finding index" - {
                val value = JsArray(JsString(VALUE))

                "then should return the value as an instance of type Defined" {
                    val lookup = value.lookup(LOCATION, IDX_ELEMENT_PATH)
                    lookup shouldBe JsLookup.Defined(LOCATION.append(IDX), JsString(VALUE))
                }
            }

            "when the value does not contain the finding index" - {
                val value = JsArray<JsString>()

                "then should return the value as an instance of type Undefined" {
                    val lookup = value.lookup(LOCATION, IDX_ELEMENT_PATH)
                    lookup shouldBe JsLookup.Undefined(LOCATION.append(IDX))
                }
            }

            "when a value is an invalid type" - {
                val value = JsString(VALUE)

                "then should return the value as an instance of type Undefined" {
                    val lookup = value.lookup(LOCATION, IDX_ELEMENT_PATH)
                    lookup shouldBe JsLookup.Undefined(LOCATION.append(IDX))
                }
            }
        }

        "The lookup by an path" - {

            "when the path contains a key element" - {

                "when the value contains the finding key" - {
                    val value = JsObject(KEY_NAME to JsString(VALUE))

                    "then should return the value as an instance of type Defined" {
                        val lookup = value.lookup(LOCATION, JsPath(KEY_ELEMENT_PATH))
                        lookup shouldBe JsLookup.Defined(LOCATION.append(KEY_NAME), JsString(VALUE))
                    }
                }

                "when the value does not contain the finding key" - {
                    val value = JsObject(KEY_NAME to JsString(VALUE))

                    "then should return the value as an instance of type Undefined" {
                        val lookup = value.lookup(LOCATION, JsPath(UNKNOWN_KEY_ELEMENT_PATH))
                        lookup shouldBe JsLookup.Undefined(LOCATION.append(UNKNOWN_KEY_NAME))
                    }
                }

                "when a value is an invalid type" - {
                    val value = JsString(VALUE)

                    "then should return the value as an instance of type Undefined" {
                        val lookup = value.lookup(LOCATION, JsPath(KEY_ELEMENT_PATH))
                        lookup shouldBe JsLookup.Undefined(LOCATION.append(KEY_NAME))
                    }
                }
            }

            "when the path contains a indexed element" - {

                "when the value contains the finding index" - {
                    val value = JsArray(JsString(VALUE))

                    "then should return the value as an instance of type Defined" {
                        val lookup = value.lookup(LOCATION, JsPath(IDX_ELEMENT_PATH))
                        lookup shouldBe JsLookup.Defined(LOCATION.append(IDX), JsString(VALUE))
                    }
                }

                "when the value does not contain the finding index" - {
                    val value = JsArray<JsString>()

                    "then should return the value as an instance of type Undefined" {
                        val lookup = value.lookup(LOCATION, JsPath(IDX_ELEMENT_PATH))
                        lookup shouldBe JsLookup.Undefined(LOCATION.append(IDX))
                    }
                }

                "when a value is an invalid type" - {
                    val value = JsString(VALUE)

                    "then should return the value as an instance of type Undefined" {
                        val lookup = value.lookup(LOCATION, JsPath(IDX_ELEMENT_PATH))
                        lookup shouldBe JsLookup.Undefined(LOCATION.append(IDX))
                    }
                }
            }
        }

        "The JsLookup#Defined" - {

            "when lookup by a key element of the path" - {

                "when the value contains the finding key" - {
                    val defined = JsLookup.Defined(LOCATION, JsObject(KEY_NAME to JsString(VALUE)))

                    "then should return the value as an instance of type Defined" {
                        val lookup = defined.apply(KEY_NAME)
                        lookup shouldBe JsLookup.Defined(LOCATION.append(KEY_NAME), JsString(VALUE))
                    }
                }

                "when the value does not contain the finding key" - {
                    val defined = JsLookup.Defined(LOCATION, JsObject(KEY_NAME to JsString(VALUE)))

                    "then should return the value as an instance of type Undefined" {
                        val lookup = defined.apply(UNKNOWN_KEY_NAME)
                        lookup shouldBe JsLookup.Undefined(LOCATION.append(UNKNOWN_KEY_NAME))
                    }
                }
            }

            "when lookup by an index element of the path" - {

                "when the value contains the finding index" - {
                    val defined = JsLookup.Defined(LOCATION, JsArray(JsString(VALUE)))

                    "then should return the value as an instance of type Defined" {
                        val lookup = defined.apply(IDX)
                        lookup shouldBe JsLookup.Defined(LOCATION.append(IDX), JsString(VALUE))
                    }
                }

                "when the value does not contain the finding index" - {
                    val defined = JsLookup.Defined(LOCATION, JsArray<JsString>())

                    "then should return the value as an instance of type Undefined" {
                        val lookup = defined.apply(IDX)
                        lookup shouldBe JsLookup.Undefined(LOCATION.append(IDX))
                    }
                }
            }
        }

        "The JsLookup#Undefined" - {
            val undefined = JsLookup.Undefined(LOCATION)

            "when lookup by a key element of the path" - {
                val lookup = undefined.apply(KEY_ELEMENT_PATH)

                "then should return the same instance of Undefined type" {
                    lookup shouldBeSameInstanceAs undefined
                }
            }

            "when lookup by an index element of the path" - {
                val lookup = undefined.apply(IDX_ELEMENT_PATH)

                "then should return the same instance of Undefined type" {
                    lookup shouldBeSameInstanceAs undefined
                }
            }
        }
    }
}
