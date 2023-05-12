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

import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsString
import io.github.airflux.serialization.core.path.JsPath
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class JsArrayTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE: String = "123"
        private const val SECOND_PHONE_VALUE: String = "456"
        private const val THIRD_PHONE_VALUE: String = "789"

        private val FIRST_ITEM = JsString(FIRST_PHONE_VALUE)
        private val SECOND_ITEM = JsString(SECOND_PHONE_VALUE)
        private val THIRD_ITEM = JsString(THIRD_PHONE_VALUE)
    }

    init {
        "The JsArray type" - {

            "The JsArray#Builder" - {

                "the `add` method" - {

                    "should add passed elements in the order" - {
                        val array = JsArray.builder()
                            .apply {
                                add(FIRST_ITEM)
                                add(SECOND_ITEM)
                            }
                            .build()

                        array shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM)
                    }
                }

                "the `addAll` method" - {

                    "should add all passed elements in the order" - {
                        val array = JsArray.builder()
                            .apply {
                                add(FIRST_ITEM)
                                addAll(listOf(SECOND_ITEM, THIRD_ITEM))
                            }
                            .build()

                        array shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM, THIRD_ITEM)
                    }
                }
            }

            "when the array is empty" - {
                val array = JsArray()

                "then the `isEmpty` method should return `true` value" {
                    array.isEmpty() shouldBe true
                }

                "then the `size` property should return the value is 0" {
                    array.size shouldBe 0
                }

                "then the `get` method for the index should return null" {
                    array[0] shouldBe null
                }

                "then the `get` method for the path element should return null" {
                    array[JsPath.Element.Idx(0)] shouldBe null
                }

                "then the toString() method should return the expected string" {
                    array shouldBeEqualsString "[]"
                }

                "then should comply with equals() and hashCode() contract" {
                    array.shouldBeEqualsContract(
                        y = JsArray(),
                        z = JsArray(),
                        other = JsArray(FIRST_ITEM)
                    )
                }
            }

            "when the array is not empty" - {
                val array = JsArray(FIRST_ITEM, SECOND_ITEM)

                "then the `isEmpty` method should return `false` value" {
                    array.isEmpty() shouldBe false
                }

                "then the `size` property should return the value is 2" {
                    array.size shouldBe 2
                }

                "then the `get` method for the index should return a specific value" - {
                    withData(
                        listOf(
                            Pair(0, FIRST_ITEM),
                            Pair(1, SECOND_ITEM)
                        )
                    ) { (index, value) ->
                        array[index] shouldBe value
                    }
                }

                "then the `get` method for the path element should return a specific value" - {
                    withData(
                        listOf(
                            Pair(JsPath.Element.Idx(0), FIRST_ITEM),
                            Pair(JsPath.Element.Idx(1), SECOND_ITEM)
                        )
                    ) { (index, value) ->
                        array[index] shouldBe value
                    }
                }

                "then the toString() method should return the expected string" {
                    array shouldBeEqualsString """["$FIRST_PHONE_VALUE", "$SECOND_PHONE_VALUE"]"""
                }

                "then should comply with equals() and hashCode() contract" {
                    array.shouldBeEqualsContract(
                        y = JsArray(FIRST_ITEM, SECOND_ITEM),
                        z = JsArray(FIRST_ITEM, SECOND_ITEM),
                        others = listOf(
                            JsArray(),
                            JsArray(FIRST_ITEM),
                            JsArray(SECOND_ITEM),
                            JsArray(SECOND_ITEM, FIRST_ITEM),
                            JsArray(FIRST_ITEM, THIRD_ITEM),
                            JsArray(FIRST_ITEM, SECOND_ITEM, THIRD_ITEM),
                        )
                    )
                }
            }

            "when creating a type value without items" - {
                val array = JsArray()

                "then should not have items" {
                    array.shouldBeEmpty()
                }
            }

            "when creating a type value with some items" - {
                val array = JsArray(FIRST_ITEM, SECOND_ITEM)

                "then should have items in the order they were passed" {
                    array shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM)
                }
            }

            "when creating a type value from a list of items" - {
                val array = JsArray(listOf(FIRST_ITEM, SECOND_ITEM))

                "then should have items in the order they were passed" {
                    array shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM)
                }
            }

            "when creating a type value using the builder" - {
                val array = JsArray.builder()
                    .apply {
                        add(FIRST_ITEM)
                        addAll(listOf(SECOND_ITEM, THIRD_ITEM))
                    }
                    .build()

                "then should have items in the order they were passed" {
                    array shouldContainExactly listOf(FIRST_ITEM, SECOND_ITEM, THIRD_ITEM)
                }
            }
        }
    }
}
