/*
 * Copyright 2021-2023 Maxim Sambulat.
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

package io.github.airflux.serialization.dsl.value

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.JsLookupResult
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsValueTest : FreeSpec() {

    companion object {
        private const val KEY_NAME = "id"
        private const val UNKNOWN_KEY_NAME = "identifier"

        private const val IDX = 0
        private const val UNKNOWN_IDX = 1

        private const val VALUE = "16945018-22fb-48fd-ab06-0740b90929d6"

        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The JsValue#div" - {

            "when lookup by a key element of the path" - {

                "when the value contains the finding key" - {
                    val json: JsValue = JsStruct(KEY_NAME to JsString(VALUE))

                    "then should return the value as an instance of type Defined" {
                        val lookup = json / KEY_NAME
                        lookup shouldBe JsLookupResult.Defined(LOCATION.append(KEY_NAME), JsString(VALUE))
                    }
                }

                "when the value does not contain the finding key" - {
                    val json: JsValue = JsStruct(KEY_NAME to JsString(VALUE))

                    "then should return the value as an instance of type Undefined#PathMissing" {
                        val lookup = json / UNKNOWN_KEY_NAME
                        lookup shouldBe JsLookupResult.Undefined.PathMissing(LOCATION.append(UNKNOWN_KEY_NAME))
                    }
                }

                "when the value is invalid element type" - {
                    val json: JsValue = JsArray(JsString(VALUE))

                    "then should return the value as an instance of type Undefined#InvalidType" {
                        val lookup = json / KEY_NAME
                        lookup shouldBe JsLookupResult.Undefined.InvalidType(
                            expected = listOf(JsStruct.nameOfType),
                            actual = JsArray.nameOfType,
                            breakpoint = LOCATION
                        )
                    }
                }
            }

            "when lookup by an index element of the path" - {

                "when the value contains the finding key" - {
                    val json: JsValue = JsArray(JsString(VALUE))

                    "then should return the value as an instance of type Defined" {
                        val lookup = json / IDX
                        lookup shouldBe JsLookupResult.Defined(LOCATION.append(IDX), JsString(VALUE))
                    }
                }

                "when the value does not contain the finding key" - {
                    val json: JsValue = JsArray(JsString(VALUE))

                    "then should return the value as an instance of type Undefined#PathMissing" {
                        val lookup = json / UNKNOWN_IDX
                        lookup shouldBe JsLookupResult.Undefined.PathMissing(LOCATION.append(UNKNOWN_IDX))
                    }
                }

                "when the value is invalid element type" - {
                    val json: JsValue = JsStruct(KEY_NAME to JsString(VALUE))

                    "then should return the value as an instance of type Undefined#InvalidType" {
                        val lookup = json / IDX
                        lookup shouldBe JsLookupResult.Undefined.InvalidType(
                            expected = listOf(JsArray.nameOfType),
                            actual = JsStruct.nameOfType,
                            breakpoint = LOCATION
                        )
                    }
                }
            }
        }
    }
}
