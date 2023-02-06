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

package io.github.airflux.serialization.core.path

import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsContract
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class PropertyPathTest : FreeSpec() {

    companion object {
        private const val USER = "user"
        private const val NAME = "name"
        private const val IDX_0 = 0
        private const val IDX_1 = 1
    }

    init {
        "A PropertyPath type" - {
            val keyUser = USER

            "create from named path element '$keyUser'" - {
                val path = PropertyPath(keyUser)

                "should have element of type PropertyPath#Element#Key with value '$keyUser'" {
                    path shouldContainExactly listOf(PropertyPath.Element.Key(keyUser))
                }

                "method 'toString() should return '#/$keyUser'" {
                    path.toString() shouldBe "#/$keyUser"
                }

                val keyName = NAME
                "append named path element '$keyName'" - {
                    val updatedPath = path.append(keyName)

                    "should have elements in the order they were added" {
                        updatedPath shouldContainExactly listOf(
                            PropertyPath.Element.Key(keyUser),
                            PropertyPath.Element.Key(keyName)
                        )
                    }

                    "method 'toString() should return '#/$keyUser/$keyName'" {
                        updatedPath.toString() shouldBe "#/$keyUser/$keyName"
                    }
                }

                val idx = IDX_0
                "append index path element '$idx'" - {
                    val updatedPath = path.append(idx)

                    "should have elements in the order they were added" {
                        updatedPath shouldContainExactly listOf(
                            PropertyPath.Element.Key(keyUser),
                            PropertyPath.Element.Idx(idx)
                        )
                    }

                    "method 'toString() should return '#/$keyUser[$idx]'" {
                        updatedPath.toString() shouldBe "#/$keyUser[$idx]"
                    }
                }
            }

            val firstIdx = IDX_0
            "create from index path element '$firstIdx'" - {
                val path = PropertyPath(firstIdx)

                "should have element of type PropertyPath#Element#Idx with value '$firstIdx'" {
                    path shouldContainExactly listOf(PropertyPath.Element.Idx(firstIdx))
                }

                "method 'toString() should return '#[$firstIdx]'" {
                    path.toString() shouldBe "#[$firstIdx]"
                }

                val keyName = NAME
                "append named path element '$keyName'" - {
                    val updatedPath = path.append(keyName)

                    "should have elements in the order they were added" {
                        updatedPath shouldContainExactly listOf(
                            PropertyPath.Element.Idx(firstIdx),
                            PropertyPath.Element.Key(keyName)
                        )
                    }

                    "method 'toString() should return '#[$firstIdx]/$keyName'" {
                        updatedPath.toString() shouldBe "#[$firstIdx]/$keyName"
                    }
                }

                val secondIdx = IDX_1
                "append index path element '$secondIdx'" - {
                    val updatedPath = path.append(secondIdx)

                    "should have elements in the order they were added" {
                        updatedPath shouldContainExactly listOf(
                            PropertyPath.Element.Idx(firstIdx),
                            PropertyPath.Element.Idx(secondIdx)
                        )
                    }

                    "method 'toString() should return '#[$firstIdx][$secondIdx]'" {
                        updatedPath.toString() shouldBe "#[$firstIdx][$secondIdx]"
                    }
                }
            }

            "should comply with equals() and hashCode() contract2" {
                PropertyPath(USER).append(NAME).shouldBeEqualsContract(
                    y = PropertyPath(USER).append(NAME),
                    z = PropertyPath(USER).append(NAME),
                    others = listOf(
                        PropertyPath(NAME),
                        PropertyPath(USER),
                        PropertyPath(USER).append(IDX_0),
                        PropertyPath(USER).append(NAME).append(IDX_0)
                    )
                )
            }
        }
    }

    private infix fun PropertyPath.shouldContainExactly(expected: Collection<PropertyPath.Element>) {
        fun PropertyPath.toList(): List<PropertyPath.Element> =
            foldLeft(mutableListOf()) { acc, elem -> acc.apply { add(elem) } }
        this.toList() shouldContainExactly expected
    }
}
