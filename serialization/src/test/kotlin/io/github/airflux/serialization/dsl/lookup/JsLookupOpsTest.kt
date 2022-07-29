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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.JsLookup
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsObject
import io.github.airflux.serialization.core.value.JsString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class JsLookupOpsTest : FreeSpec() {

    companion object {
        private const val KEY_NAME = "id"
        private const val UNKNOWN_KEY_NAME = "identifier"

        private const val IDX = 0
        private const val UNKNOWN_IDX = 1

        private const val VALUE = "16945018-22fb-48fd-ab06-0740b90929d6"

        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsLookup#div" - {

            "when lookup by a key element of the path" - {

                "when the receiver is of type Defined" - {
                    val defined = JsLookup.Defined(LOCATION, JsObject(KEY_NAME to JsString(VALUE)))

                    "when the value contains the finding key" - {

                        "then should return the value as an instance of type Defined" {
                            val lookup = defined / KEY_NAME
                            lookup shouldBe JsLookup.Defined(LOCATION.append(KEY_NAME), JsString(VALUE))
                        }
                    }

                    "when the value does not contain the finding key" - {

                        "then should return the value as an instance of type Undefined" {
                            val lookup = defined / UNKNOWN_KEY_NAME
                            lookup shouldBe JsLookup.Undefined(LOCATION.append(UNKNOWN_KEY_NAME))
                        }
                    }
                }

                "when the receiver is of type Undefined" - {
                    val undefined = JsLookup.Undefined(LOCATION)

                    "then should return the same instance of Undefined type" {
                        val lookup = undefined / KEY_NAME
                        lookup shouldBeSameInstanceAs undefined
                    }
                }
            }

            "when lookup by an index element of the path" - {

                "when the receiver is of type Defined" - {
                    val defined = JsLookup.Defined(LOCATION, JsArray(JsString(VALUE)))

                    "when the value contains the finding index" - {

                        "then should return the value as an instance of type Defined" {
                            val lookup = defined / IDX
                            lookup shouldBe JsLookup.Defined(LOCATION.append(IDX), JsString(VALUE))
                        }
                    }

                    "when the value does not contain the finding index" - {

                        "then should return the value as an instance of type Undefined" {
                            val lookup = defined / UNKNOWN_IDX
                            lookup shouldBe JsLookup.Undefined(LOCATION.append(UNKNOWN_IDX))
                        }
                    }
                }

                "when the receiver is of type Undefined" - {
                    val undefined = JsLookup.Undefined(LOCATION)

                    "then should return the same instance of Undefined type" {
                        val lookup = undefined / IDX
                        lookup shouldBeSameInstanceAs undefined
                    }
                }
            }
        }
    }
}