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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.core.path.JsPath
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class ContainsTest : FreeSpec() {

    companion object {
        private const val USER_NAME_VALUE = "user"
        private const val PHONE_NUMBER_VALUE = "123456789"
        val SOURCE: JsStruct = JsStruct(
            "user" to JsStruct(
                "name" to JsString(USER_NAME_VALUE),
                "phones" to JsArray(
                    JsStruct(
                        "value" to JsString(PHONE_NUMBER_VALUE)
                    )
                )
            )
        )
    }

    init {

        "JsStruct#contains" - {

            "should return the true value if an element by the path was found" - {
                withData(
                    nameFn = { "$it" },
                    listOf(
                        JsPath("user"),
                        JsPath("user").append("name"),
                        JsPath("user").append("phones"),
                        JsPath("user").append("phones").append(0),
                        JsPath("user").append("phones").append(0).append("value"),
                    )
                ) { path ->
                    val result = path in SOURCE
                    result shouldBe true
                }
            }

            "should return the false value if an element by the path was found" - {
                withData(
                    nameFn = { "$it" },
                    listOf(
                        JsPath("id"),
                        JsPath("user").append("id"),
                        JsPath("user").append(0),
                        JsPath("user").append("phones").append("title"),
                        JsPath("user").append("phones").append(1),
                        JsPath("user").append("phones").append(0).append("title"),
                    )
                ) { path ->
                    val result = path in SOURCE
                    result shouldBe false
                }
            }
        }
    }
}
