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

package io.github.airflux.serialization.core.path

import io.github.airflux.serialization.kotest.assertions.shouldBeEqualsContract
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class JsPathTest : FreeSpec() {

    companion object {
        private const val USER = "user"
        private const val NAME = "name"
        private const val IDX_0 = 0
        private const val IDX_1 = 1
    }

    init {
        "A JsPath type" - {
            val keyUser = USER

            "create from named path element '$keyUser'" - {
                val path = JsPath(keyUser)

                "should have element of type JsPath#Element#Key with value '$keyUser'" {
                    path shouldContainExactly listOf(JsPath.Element.Key(keyUser))
                }

                "method 'toString() should return '#/$keyUser'" {
                    path.toString() shouldBe "#/$keyUser"
                }

                "should comply with equals() and hashCode() contract" {
                    JsPath(USER).shouldBeEqualsContract(
                        y = JsPath(USER),
                        z = JsPath(USER),
                        others = listOf(
                            JsPath(NAME),
                            JsPath(USER).append(IDX_0),
                            JsPath(USER).append(NAME).append(IDX_0)
                        )
                    )
                }

                val keyName = NAME
                "append named path element '$keyName'" - {
                    val updatedPath = path.append(keyName)

                    "should have elements in the order they were added" {
                        updatedPath shouldContainExactly listOf(
                            JsPath.Element.Key(keyUser),
                            JsPath.Element.Key(keyName)
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
                            JsPath.Element.Key(keyUser),
                            JsPath.Element.Idx(idx)
                        )
                    }

                    "method 'toString() should return '#/$keyUser[$idx]'" {
                        updatedPath.toString() shouldBe "#/$keyUser[$idx]"
                    }
                }
            }

            val firstIdx = IDX_0
            "create from index path element '$firstIdx'" - {
                val path = JsPath(firstIdx)

                "should have element of type JsPath#Element#Idx with value '$firstIdx'" {
                    path shouldContainExactly listOf(JsPath.Element.Idx(firstIdx))
                }

                "method 'toString() should return '#[$firstIdx]'" {
                    path.toString() shouldBe "#[$firstIdx]"
                }

                "should comply with equals() and hashCode() contract" {
                    JsPath(IDX_0).shouldBeEqualsContract(
                        y = JsPath(IDX_0),
                        z = JsPath(IDX_0),
                        others = listOf(
                            JsPath(IDX_1),
                            JsPath(USER).append(IDX_0),
                            JsPath(USER).append(NAME).append(IDX_0)
                        )
                    )
                }

                val keyName = NAME
                "append named path element '$keyName'" - {
                    val updatedPath = path.append(keyName)

                    "should have elements in the order they were added" {
                        updatedPath shouldContainExactly listOf(
                            JsPath.Element.Idx(firstIdx),
                            JsPath.Element.Key(keyName)
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
                            JsPath.Element.Idx(firstIdx),
                            JsPath.Element.Idx(secondIdx)
                        )
                    }

                    "method 'toString() should return '#[$firstIdx][$secondIdx]'" {
                        updatedPath.toString() shouldBe "#[$firstIdx][$secondIdx]"
                    }
                }
            }

            "should comply with equals() and hashCode() contract" {
                JsPath(USER).append(NAME).shouldBeEqualsContract(
                    y = JsPath(USER).append(NAME),
                    z = JsPath(USER).append(NAME),
                    others = listOf(
                        JsPath(NAME),
                        JsPath(USER),
                        JsPath(USER).append(IDX_0),
                        JsPath(USER).append(NAME).append(IDX_0)
                    )
                )
            }
        }
    }

    private infix fun JsPath.shouldContainExactly(expected: Collection<JsPath.Element>) {
        fun JsPath.toList(): List<JsPath.Element> =
            foldLeft(mutableListOf()) { acc, elem -> acc.apply { add(elem) } }
        this.toList() shouldContainExactly expected
    }
}
