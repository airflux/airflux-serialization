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

package io.github.airflux.serialization.dsl.value

import io.github.airflux.serialization.common.TestData.USER_NAME_VALUE
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.JsLookup
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsObject
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsValueOpsTest : FreeSpec() {

    init {
        "The 'div' extension-function" - {
            "called with a parameter of a key path element" - {
                "returns the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))

                    val lookup = json / "name"

                    lookup shouldBe JsLookup.Defined(JsLocation.empty.append("name"), JsString(USER_NAME_VALUE))
                }
                "returns the 'JsLookup.Undefined.PathMissing' if a node is not found" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME_VALUE))

                    val lookup = json / "user"

                    lookup shouldBe JsLookup.Undefined.PathMissing(JsLocation.empty.append("user"))
                }
                "returns the 'JsLookup.Undefined.InvalidType' if a node element is invalid type" {
                    val json: JsValue = JsString(USER_NAME_VALUE)

                    val lookup = json / "user"

                    lookup shouldBe JsLookup.Undefined.InvalidType(
                        location = JsLocation.empty,
                        expected = JsValue.Type.OBJECT,
                        actual = JsValue.Type.STRING
                    )
                }
            }
            "called with a parameter of an idx path element" - {
                "returns the 'JsLookup.Defined' if a node is found" {
                    val json: JsValue = JsArray(JsString(USER_NAME_VALUE))

                    val lookup = json / 0

                    lookup shouldBe JsLookup.Defined(JsLocation.empty.append(0), JsString(USER_NAME_VALUE))
                }
                "returns the 'JsLookup.Undefined.PathMissing' if a node is not found return" {
                    val json: JsValue = JsArray(JsString(USER_NAME_VALUE))

                    val lookup = json / 1

                    lookup shouldBe JsLookup.Undefined.PathMissing(JsLocation.empty.append(1))
                }
                "returns the 'JsLookup.Undefined.InvalidType' if a node element is invalid type" {
                    val json: JsValue = JsString(USER_NAME_VALUE)

                    val lookup = json / 0

                    lookup shouldBe JsLookup.Undefined.InvalidType(
                        location = JsLocation.empty,
                        expected = JsValue.Type.ARRAY,
                        actual = JsValue.Type.STRING
                    )
                }
            }
        }
    }
}
