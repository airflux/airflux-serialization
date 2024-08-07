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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.kotest.assertions.shouldBeEqualsContract
import io.github.airflux.serialization.kotest.assertions.shouldBeEqualsString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsStringTest : FreeSpec() {

    companion object {
        private const val TEXT = "text"
    }

    init {
        "The JsString type" - {

            "when created" - {
                val value = JsString(TEXT)

                "then the inner value should be true" {
                    value.get shouldBe TEXT
                }

                "then the toString() method should return the expected string" {
                    value shouldBeEqualsString "JsString($TEXT)"
                }

                "should comply with equals() and hashCode() contract" {
                    JsString(TEXT).shouldBeEqualsContract(
                        y = JsString(TEXT),
                        z = JsString(TEXT),
                        other = JsString("")
                    )
                }
            }
        }
    }
}
