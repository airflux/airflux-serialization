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

package io.github.airflux.serialization.dsl.path

import io.github.airflux.serialization.core.path.JsPath
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class JsPathTest : FreeSpec() {

    init {

        "The JsPath type" - {

            "JsPath#Companion#div(String) function" - {
                val path = JsPath / "user"

                "should have elements in the order they were passed" {
                    path shouldContainExactly listOf(JsPath.Element.Key("user"))
                }
            }

            "JsPath#Companion#div(Int) function" - {
                val path = JsPath / 0

                "should have elements in the order they were passed" {
                    path shouldContainExactly listOf(JsPath.Element.Idx(0))
                }
            }

            "JsPath#div(String) function" - {
                val path = JsPath / "user" / "name"

                "should have elements in the order they were passed" {
                    path shouldContainExactly listOf(
                        JsPath.Element.Key("user"),
                        JsPath.Element.Key("name")
                    )
                }
            }

            "JsPath#div(Int) function" - {
                val path = JsPath / "phones" / 0

                "should have elements in the order they were passed" {
                    path shouldContainExactly listOf(
                        JsPath.Element.Key("phones"),
                        JsPath.Element.Idx(0)
                    )
                }
            }
        }
    }

    private infix fun JsPath.shouldContainExactly(expected: Collection<JsPath.Element>) {
        fun JsPath.toList(): List<JsPath.Element> =
            foldLeft(mutableListOf()) { acc, elem -> acc.apply { add(elem) } }
        this.toList() shouldContainExactly expected
    }
}
