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

package io.github.airflux.serialization.dsl.path

import io.github.airflux.serialization.core.path.PropertyPath
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class PropertyPathTest : FreeSpec() {

    init {

        "The PropertyPath type" - {

            "PropertyPath#Companion#div(String) function" - {
                val path = PropertyPath / "user"

                "should have elements in the order they were passed" {
                    path shouldContainExactly listOf(PropertyPath.Element.Key("user"))
                }
            }

            "PropertyPath#Companion#div(Int) function" - {
                val path = PropertyPath / 0

                "should have elements in the order they were passed" {
                    path shouldContainExactly listOf(PropertyPath.Element.Idx(0))
                }
            }

            "PropertyPath#div(String) function" - {
                val path = PropertyPath / "user" / "name"

                "should have elements in the order they were passed" {
                    path shouldContainExactly listOf(
                        PropertyPath.Element.Key("user"),
                        PropertyPath.Element.Key("name")
                    )
                }
            }

            "PropertyPath#div(Int) function" - {
                val path = PropertyPath / "phones" / 0

                "should have elements in the order they were passed" {
                    path shouldContainExactly listOf(
                        PropertyPath.Element.Key("phones"),
                        PropertyPath.Element.Idx(0)
                    )
                }
            }
        }
    }

    internal infix fun PropertyPath.shouldContainExactly(expected: Collection<PropertyPath.Element>) {
        fun PropertyPath.toList(): List<PropertyPath.Element> =
            foldLeft(mutableListOf()) { acc, elem -> acc.apply { add(elem) } }
        this.toList() shouldContainExactly expected
    }
}
