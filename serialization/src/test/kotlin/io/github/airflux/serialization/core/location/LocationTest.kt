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
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class LocationTest : FreeSpec() {

    companion object {
        private const val USER = "users"
        private const val PHONE = "phone"
        private const val MOBILE = "mobile"
        private const val TITLE = "title"
        private const val IDX = 0
    }

    init {
        "The Location type" - {

            "when empty" - {
                val location = Location.empty

                "should be empty" {
                    location.isEmpty shouldBe true
                }

                "the foldLeft() method should perform the fold to the left" {
                    val result: List<PropertyPath.Element> = location.foldLeft(mutableListOf()) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly emptyList()
                }

                "the foldRight() method should perform the fold to the right" {
                    val result: List<PropertyPath.Element> = location.foldRight(mutableListOf()) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly emptyList()
                }

                "the append() method with the PropertyPath type parameter" {
                    val path = PropertyPath(USER).append(IDX).append(PHONE)
                    val newLocation = location.append(path)

                    val result: List<PropertyPath.Element> = newLocation.toRightList()
                    result shouldContainExactly listOf(
                        PropertyPath.Element.Key(USER),
                        PropertyPath.Element.Idx(IDX),
                        PropertyPath.Element.Key(PHONE)
                    )
                }

                "the append() method with the parameter of the path" {
                    val path = PropertyPath(USER).append(IDX).append(PHONE)
                    val newLocation = location.append(path)

                    val result: List<PropertyPath.Element> = newLocation.toRightList()
                    result shouldContainExactly listOf(
                        PropertyPath.Element.Key(USER),
                        PropertyPath.Element.Idx(IDX),
                        PropertyPath.Element.Key(PHONE)
                    )
                }

                "the 'toString() method should return '#'" {
                    location.toString() shouldBe "#"
                }
            }

            "when non-empty" - {
                val location = Location.empty.append(USER).append(IDX).append(PHONE)

                "should be non-empty" {
                    location.isEmpty shouldBe false
                }

                "the foldLeft() method should perform the fold to the left" {
                    val result: List<PropertyPath.Element> = location.foldLeft(mutableListOf()) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PropertyPath.Element.Key(PHONE),
                        PropertyPath.Element.Idx(IDX),
                        PropertyPath.Element.Key(USER)
                    )
                }

                "the foldRight() method should perform the fold to the right" {
                    val result: List<PropertyPath.Element> = location.foldRight(mutableListOf()) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        PropertyPath.Element.Key(USER),
                        PropertyPath.Element.Idx(IDX),
                        PropertyPath.Element.Key(PHONE)
                    )
                }

                "the append() method with the PropertyPathF type parameter" {
                    val path = PropertyPath(MOBILE).append(TITLE)
                    val newLocation = location.append(path)

                    val result: List<PropertyPath.Element> = newLocation.toRightList()
                    result shouldContainExactly listOf(
                        PropertyPath.Element.Key(USER),
                        PropertyPath.Element.Idx(IDX),
                        PropertyPath.Element.Key(PHONE),
                        PropertyPath.Element.Key(MOBILE),
                        PropertyPath.Element.Key(TITLE)
                    )
                }

                "the append() method with the parameter of the path" {
                    val path = PropertyPath(MOBILE).append(IDX).append(TITLE)
                    val newLocation = location.append(path)

                    val result: List<PropertyPath.Element> = newLocation.toRightList()
                    result shouldContainExactly listOf(
                        PropertyPath.Element.Key(USER),
                        PropertyPath.Element.Idx(IDX),
                        PropertyPath.Element.Key(PHONE),
                        PropertyPath.Element.Key(MOBILE),
                        PropertyPath.Element.Idx(IDX),
                        PropertyPath.Element.Key(TITLE)
                    )
                }

                "the 'toString() method should return '#/users[0]/phone'" {
                    location.toString() shouldBe "#/users[0]/phone"
                }
            }

            "should comply with equals() and hashCode() contract" {
                Location.empty.append(USER).append(PHONE).shouldBeEqualsContract(
                    y = Location.empty.append(USER).append(PHONE),
                    z = Location.empty.append(USER).append(PHONE),
                    others = listOf(
                        Location.empty,
                        Location.empty.append(USER),
                        Location.empty.append(PHONE),
                        Location.empty.append(USER).append(IDX),
                        Location.empty.append(USER).append(PHONE).append(IDX)
                    )
                )
            }
        }
    }

    internal fun Location.toRightList(): List<PropertyPath.Element> =
        foldRight(mutableListOf()) { acc, elem -> acc.apply { add(elem) } }
}
