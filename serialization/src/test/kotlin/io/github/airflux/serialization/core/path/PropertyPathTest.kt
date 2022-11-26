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

package io.github.airflux.serialization.core.path

import io.github.airflux.serialization.common.kotest.shouldBeEqualsContract
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class PropertyPathTest : FreeSpec() {

    companion object {
        private val OTHER_PATH = PropertyPath("other")
    }

    init {
        "A PropertyPath type" - {
            val keyUser = "user"

            "create from named path element '$keyUser'" - {
                val path = PropertyPath(keyUser)

                "should have only one element" {
                    path.elements.size shouldBe 1
                }

                "should have element of type PropertyPath#Element#Key with value '$keyUser'" {
                    path.elements shouldContain PropertyPath.Element.Key(keyUser)
                }

                "method 'toString() should return '#/$keyUser'" {
                    path.toString() shouldBe "#/$keyUser"
                }

                "should comply with equals() and hashCode() contract" {
                    path.shouldBeEqualsContract(
                        y = PropertyPath(keyUser),
                        z = PropertyPath(keyUser),
                        other = OTHER_PATH
                    )
                }

                val keyName = "name"
                "append named path element '$keyName'" - {
                    val updatedPath = path.append(keyName)

                    "should have two elements" {
                        updatedPath.elements.size shouldBe 2
                    }

                    "should have elements in the order they were added" {
                        updatedPath.elements shouldContainExactly listOf(
                            PropertyPath.Element.Key(keyUser),
                            PropertyPath.Element.Key(keyName)
                        )
                    }

                    "method 'toString() should return '#/$keyUser/$keyName'" {
                        updatedPath.toString() shouldBe "#/$keyUser/$keyName"
                    }

                    "should comply with equals() and hashCode() contract" {
                        updatedPath.shouldBeEqualsContract(
                            y = PropertyPath(keyUser).append(keyName),
                            z = PropertyPath(keyUser).append(keyName),
                            other = OTHER_PATH
                        )
                    }
                }

                val idx = 0
                "append index path element '$idx'" - {
                    val updatedPath = path.append(idx)

                    "should have two elements" {
                        updatedPath.elements.size shouldBe 2
                    }

                    "should have elements in the order they were added" {
                        updatedPath.elements shouldContainExactly listOf(
                            PropertyPath.Element.Key(keyUser),
                            PropertyPath.Element.Idx(idx)
                        )
                    }

                    "method 'toString() should return '#/$keyUser[$idx]'" {
                        updatedPath.toString() shouldBe "#/$keyUser[$idx]"
                    }

                    "should comply with equals() and hashCode() contract" {
                        updatedPath.shouldBeEqualsContract(
                            y = PropertyPath(keyUser).append(idx),
                            z = PropertyPath(keyUser).append(idx),
                            other = OTHER_PATH
                        )
                    }
                }
            }

            val firstIdx = 0
            "create from index path element '$firstIdx'" - {
                val path = PropertyPath(firstIdx)

                "should have only one element" {
                    path.elements.size shouldBe 1
                }

                "should have element of type PropertyPath#Element#Idx with value '$firstIdx'" {
                    path.elements[0].shouldBeInstanceOf<PropertyPath.Element.Idx>().get shouldBe firstIdx
                }

                "method 'toString() should return '#[$firstIdx]'" {
                    path.toString() shouldBe "#[$firstIdx]"
                }

                "should comply with equals() and hashCode() contract" {
                    path.shouldBeEqualsContract(
                        y = PropertyPath(firstIdx),
                        z = PropertyPath(firstIdx),
                        other = OTHER_PATH
                    )
                }

                val keyName = "name"
                "append named path element '$keyName'" - {
                    val updatedPath = path.append(keyName)

                    "should have two elements" {
                        updatedPath.elements.size shouldBe 2
                    }

                    "should have elements in the order they were added" {
                        updatedPath.elements shouldContainExactly listOf(
                            PropertyPath.Element.Idx(firstIdx),
                            PropertyPath.Element.Key(keyName)
                        )
                    }

                    "method 'toString() should return '#[$firstIdx]/$keyName'" {
                        updatedPath.toString() shouldBe "#[$firstIdx]/$keyName"
                    }

                    "should comply with equals() and hashCode() contract" {
                        updatedPath.shouldBeEqualsContract(
                            y = PropertyPath(firstIdx).append(keyName),
                            z = PropertyPath(firstIdx).append(keyName),
                            other = OTHER_PATH
                        )
                    }
                }

                val secondIdx = 1
                "append index path element '$secondIdx'" - {
                    val updatedPath = path.append(secondIdx)

                    "should have two elements" {
                        updatedPath.elements.size shouldBe 2
                    }

                    "should have elements in the order they were added" {
                        updatedPath.elements shouldContainExactly listOf(
                            PropertyPath.Element.Idx(firstIdx),
                            PropertyPath.Element.Idx(secondIdx)
                        )
                    }

                    "method 'toString() should return '#[$firstIdx][$secondIdx]'" {
                        updatedPath.toString() shouldBe "#[$firstIdx][$secondIdx]"
                    }

                    "should comply with equals() and hashCode() contract" {
                        updatedPath.shouldBeEqualsContract(
                            y = PropertyPath(firstIdx).append(secondIdx),
                            z = PropertyPath(firstIdx).append(secondIdx),
                            other = OTHER_PATH
                        )
                    }
                }
            }
        }
    }
}
