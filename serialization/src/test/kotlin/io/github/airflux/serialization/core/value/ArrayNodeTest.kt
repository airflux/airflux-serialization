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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.common.kotest.shouldBeEqualsString
import io.github.airflux.serialization.core.path.PropertyPath
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ArrayNodeTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE: String = "123"
        private const val SECOND_PHONE_VALUE: String = "456"

        private val FIRST_ITEM = StringNode(FIRST_PHONE_VALUE)
        private val SECOND_ITEM = StringNode(SECOND_PHONE_VALUE)
    }

    init {
        "The ArrayNode type" - {

            "when created without elements" - {
                val array = ArrayNode<StringNode>()

                "should be empty" {
                    array.isEmpty() shouldBe true
                }

                "should have size 0" {
                    array.size shouldBe 0
                }

                "should not have elements" {
                    array.shouldBeEmpty()
                }

                "then the method of getting the value of the element by index should return null" {
                    array[0] shouldBe null
                }

                "then the method of getting the value of the element by path element should return null" {
                    array[PropertyPath.Element.Idx(0)] shouldBe null
                }

                "then the toString() method should return the expected string" {
                    array shouldBeEqualsString "[]"
                }
            }

            "when created with some elements" - {
                val array = ArrayNode(FIRST_ITEM, SECOND_ITEM)

                "should be non-empty" {
                    array.isEmpty() shouldBe false
                }

                "should have size 2" {
                    array.size shouldBe 2
                }

                "should have elements in the order they were added" {
                    array shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM)
                }

                "then the method of getting the value of the element by index should return a specific value" - {
                    withData(
                        listOf(
                            Pair(0, FIRST_ITEM),
                            Pair(1, SECOND_ITEM)
                        )
                    ) { (index, value) ->
                        array[index] shouldBe value
                    }
                }

                "then the method of getting the value of the element by path element should return a specific value" - {
                    withData(
                        listOf(
                            Pair(PropertyPath.Element.Idx(0), FIRST_ITEM),
                            Pair(PropertyPath.Element.Idx(1), SECOND_ITEM)
                        )
                    ) { (index, value) ->
                        array[index] shouldBe value
                    }
                }

                "then the toString() method should return the expected string" {
                    array shouldBeEqualsString """["$FIRST_PHONE_VALUE", "$SECOND_PHONE_VALUE"]"""
                }
            }

            "should comply with equals() and hashCode() contract" {
                ArrayNode(FIRST_ITEM).shouldBeEqualsContract(
                    y = ArrayNode(FIRST_ITEM),
                    z = ArrayNode(FIRST_ITEM),
                    others = listOf(
                        ArrayNode(),
                        ArrayNode(FIRST_ITEM, SECOND_ITEM)
                    )
                )
            }
        }
    }
}
