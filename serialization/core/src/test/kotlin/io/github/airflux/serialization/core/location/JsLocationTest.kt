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

package io.github.airflux.serialization.core.location

import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.test.kotest.shouldBeEqualsContract
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class JsLocationTest : FreeSpec() {

    companion object {
        private const val USER = "users"
        private const val PHONE = "phone"
        private const val MOBILE = "mobile"
        private const val TITLE = "title"
        private const val IDX = 0
    }

    init {
        "The JsLocation type" - {

            "when empty" - {
                val location = JsLocation

                "should be empty" {
                    location.isEmpty shouldBe true
                }

                "the foldLeft() method should perform the fold to the left" {
                    val result: List<JsPath.Element> = location.foldLeft(mutableListOf()) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly emptyList()
                }

                "the foldRight() method should perform the fold to the right" {
                    val result: List<JsPath.Element> = location.foldRight(mutableListOf()) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly emptyList()
                }

                "the append() method with the JsPath type parameter" {
                    val path = JsPath(USER).append(IDX).append(PHONE)
                    val newLocation = location.append(path)

                    val result: List<JsPath.Element> = newLocation.toRightList()
                    result shouldContainExactly listOf(
                        JsPath.Element.Key(USER),
                        JsPath.Element.Idx(IDX),
                        JsPath.Element.Key(PHONE)
                    )
                }

                "the append() method with the parameter of the path" {
                    val path = JsPath(USER).append(IDX).append(PHONE)
                    val newLocation = location.append(path)

                    val result: List<JsPath.Element> = newLocation.toRightList()
                    result shouldContainExactly listOf(
                        JsPath.Element.Key(USER),
                        JsPath.Element.Idx(IDX),
                        JsPath.Element.Key(PHONE)
                    )
                }

                "the 'toString() method should return '#'" {
                    location.toString() shouldBe "#"
                }
            }

            "when non-empty" - {
                val location = JsLocation.append(USER).append(IDX).append(PHONE)

                "should be non-empty" {
                    location.isEmpty shouldBe false
                }

                "the foldLeft() method should perform the fold to the left" {
                    val result: List<JsPath.Element> = location.foldLeft(mutableListOf()) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        JsPath.Element.Key(PHONE),
                        JsPath.Element.Idx(IDX),
                        JsPath.Element.Key(USER)
                    )
                }

                "the foldRight() method should perform the fold to the right" {
                    val result: List<JsPath.Element> = location.foldRight(mutableListOf()) { acc, elem ->
                        acc.apply { add(elem) }
                    }
                    result shouldContainExactly listOf(
                        JsPath.Element.Key(USER),
                        JsPath.Element.Idx(IDX),
                        JsPath.Element.Key(PHONE)
                    )
                }

                "the append() method with the PropertyPathF type parameter" {
                    val path = JsPath(MOBILE).append(TITLE)
                    val newLocation = location.append(path)

                    val result: List<JsPath.Element> = newLocation.toRightList()
                    result shouldContainExactly listOf(
                        JsPath.Element.Key(USER),
                        JsPath.Element.Idx(IDX),
                        JsPath.Element.Key(PHONE),
                        JsPath.Element.Key(MOBILE),
                        JsPath.Element.Key(TITLE)
                    )
                }

                "the append() method with the parameter of the path" {
                    val path = JsPath(MOBILE).append(IDX).append(TITLE)
                    val newLocation = location.append(path)

                    val result: List<JsPath.Element> = newLocation.toRightList()
                    result shouldContainExactly listOf(
                        JsPath.Element.Key(USER),
                        JsPath.Element.Idx(IDX),
                        JsPath.Element.Key(PHONE),
                        JsPath.Element.Key(MOBILE),
                        JsPath.Element.Idx(IDX),
                        JsPath.Element.Key(TITLE)
                    )
                }

                "the 'toString() method should return '#/users[0]/phone'" {
                    location.toString() shouldBe "#/users[0]/phone"
                }
            }

            "should comply with equals() and hashCode() contract" {
                JsLocation.append(USER).append(PHONE).shouldBeEqualsContract(
                    y = JsLocation.append(USER).append(PHONE),
                    z = JsLocation.append(USER).append(PHONE),
                    others = listOf(
                        JsLocation,
                        JsLocation.append(USER),
                        JsLocation.append(PHONE),
                        JsLocation.append(USER).append(IDX),
                        JsLocation.append(USER).append(PHONE).append(IDX)
                    )
                )
            }
        }
    }

    private fun JsLocation.toRightList(): List<JsPath.Element> =
        foldRight(mutableListOf()) { acc, elem -> acc.apply { add(elem) } }
}
