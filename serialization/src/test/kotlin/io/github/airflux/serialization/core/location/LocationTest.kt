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

package io.github.airflux.serialization.core.location

import io.github.airflux.serialization.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPathElement
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class LocationTest : FreeSpec() {

    init {
        "The Location type" - {

            "when empty" - {
                val location = Location.empty

                "should be empty" {
                    location.isEmpty shouldBe true
                }

                "the foldRight() method should perform the fold to the right" {
                    val result: List<PropertyPathElement> = Location.foldRight(mutableListOf(), location) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly emptyList()
                }

                "the foldLeft() method should perform the fold to the left" {
                    val result: List<PropertyPathElement> = Location.foldLeft(mutableListOf(), location) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly emptyList()
                }

                "the append() method with the PropertyPath type parameter" {
                    val path = PropertyPath("users").append(0).append("phone")
                    val newLocation = location.append(path)

                    val result: List<PropertyPathElement> = Location.foldLeft(mutableListOf(), newLocation) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PropertyPathElement.Key("users"),
                        PropertyPathElement.Idx(0),
                        PropertyPathElement.Key("phone")
                    )
                }

                "the append() method with the parameter of path element collection type" {
                    val path = listOf(PropertyPathElement.Key("users"), PropertyPathElement.Idx(0), PropertyPathElement.Key("phone"))
                    val newLocation = location.append(path)

                    val result: List<PropertyPathElement> = Location.foldLeft(mutableListOf(), newLocation) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PropertyPathElement.Key("users"),
                        PropertyPathElement.Idx(0),
                        PropertyPathElement.Key("phone")
                    )
                }

                "the 'toString() method should return '#'" {
                    location.toString() shouldBe "#"
                }

                "should comply with equals() and hashCode() contract" {
                    location.shouldBeEqualsContract(
                        y = Location.empty,
                        z = Location.empty,
                        other = Location.empty.append("user")
                    )
                }
            }

            "when non-empty" - {
                val location = Location.empty.append("users").append(0).append("phone")

                "should be non-empty" {
                    location.isEmpty shouldBe false
                }

                "the foldRight() method should perform the fold to the right" {
                    val result: List<PropertyPathElement> = Location.foldRight(mutableListOf(), location) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PropertyPathElement.Key("phone"),
                        PropertyPathElement.Idx(0),
                        PropertyPathElement.Key("users")
                    )
                }

                "the foldLeft() method should perform the fold to the left" {
                    val result: List<PropertyPathElement> = Location.foldLeft(mutableListOf(), location) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PropertyPathElement.Key("users"),
                        PropertyPathElement.Idx(0),
                        PropertyPathElement.Key("phone")
                    )
                }

                "the append() method with the PropertyPathF type parameter" {
                    val path = PropertyPath("mobile").append("title")
                    val newLocation = location.append(path)

                    val result: List<PropertyPathElement> = Location.foldLeft(mutableListOf(), newLocation) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PropertyPathElement.Key("users"),
                        PropertyPathElement.Idx(0),
                        PropertyPathElement.Key("phone"),
                        PropertyPathElement.Key("mobile"),
                        PropertyPathElement.Key("title")
                    )
                }

                "the append() method with the parameter of path element collection type" {
                    val path = listOf(PropertyPathElement.Key("mobile"), PropertyPathElement.Idx(0), PropertyPathElement.Key("title"))
                    val newLocation = location.append(path)

                    val result: List<PropertyPathElement> = Location.foldLeft(mutableListOf(), newLocation) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PropertyPathElement.Key("users"),
                        PropertyPathElement.Idx(0),
                        PropertyPathElement.Key("phone"),
                        PropertyPathElement.Key("mobile"),
                        PropertyPathElement.Idx(0),
                        PropertyPathElement.Key("title")
                    )
                }

                "the 'toString() method should return '#/users[0]/phone'" {
                    location.toString() shouldBe "#/users[0]/phone"
                }

                "should comply with equals() and hashCode() contract" {
                    location.shouldBeEqualsContract(
                        y = Location.empty.append("users").append(0).append("phone"),
                        z = Location.empty.append("users").append(0).append("phone"),
                        other = Location.empty
                    )
                }
            }
        }
    }
}
