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

package io.github.airflux.serialization.core.common

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class NonEmptyListTest : FreeSpec() {

    init {

        "The `NonEmptyList` type" - {

            "when only one element is passed to create an instance of the type" - {
                val list = NonEmptyList(FIRST)

                "then the new instance of the type should only contain the passed element" {
                    list.items shouldContainExactly listOf(FIRST)
                }
            }

            "when a few element is passed to create an instance of the type" - {
                val list = NonEmptyList(FIRST, SECOND)

                "then the new instance of the type should only contain the passed elements in the order in which they were passed" {
                    list.items shouldContainExactly listOf(FIRST, SECOND)
                }
            }

            "when a head element and empty tail list are passed to create an instance of the type" - {
                val list = NonEmptyList(FIRST, emptyList())

                "then the new instance of the type should only contain the passed head element" {
                    list.items shouldContainExactly listOf(FIRST)
                }
            }

            "when a head element and non-empty tail list are passed to create an instance of the type" - {
                val list = NonEmptyList(FIRST, listOf(SECOND, THIRD))

                "then the new instance of the type should all contain the passed elements in the order in which they were passed" {
                    list.items shouldContainExactly listOf(FIRST, SECOND, THIRD)
                }
            }

            "when an empty list is passed to create an instance of the type" - {
                val list = NonEmptyList.valueOf(emptyList<Int>())

                "then should return the null value" {
                    list.shouldBeNull()
                }
            }

            "when a non-empty list is passed to create an instance of the type" - {
                val list = NonEmptyList.valueOf(listOf(FIRST, SECOND, THIRD))

                "then the new instance of the type should contain all the elements from the list in the order in which they were passed" {
                    list.shouldNotBeNull()
                    list.items shouldContainExactly listOf(FIRST, SECOND, THIRD)
                }
            }

            "when a new element is added to the instance of the type" - {
                val list = NonEmptyList(FIRST) + SECOND

                "then the new instance of the type should contain the original elements and the passed element in the order in which they were passed" {
                    list.items shouldContainExactly listOf(FIRST, SECOND)
                }
            }

            "when a list of elements is added to the instance of the type" - {

                "when the list of elements is empty" - {
                    val list = NonEmptyList(FIRST) + emptyList()

                    "then the new instance of the type should contain only the original element" {
                        list.items shouldContainExactly listOf(FIRST)
                    }
                }

                "when the list of elements is not empty" - {
                    val list = NonEmptyList(FIRST) + listOf(SECOND, THIRD)

                    "then the new instance of the type should contain elements from the original instance and the passed elements in the order in which they were passed" {
                        list.items shouldContainExactly listOf(FIRST, SECOND, THIRD)
                    }
                }
            }

            "when another instance of the type is added to the instance of the type" - {
                val list = NonEmptyList(FIRST) + NonEmptyList(SECOND, THIRD)

                "then the new instance of the type should contain elements from both instances in the order in which they were in the originals" {
                    list.items shouldContainExactly listOf(FIRST, SECOND, THIRD)
                }
            }

            "when searching for an element in the list" - {

                "when the element is in the list" - {
                    val list = NonEmptyList(FIRST, SECOND, THIRD)

                    "then the `exists` function should return true" {
                        list.exists { it == SECOND } shouldBe true
                    }
                }

                "when the element is not in the list" - {
                    val list = NonEmptyList(FIRST, SECOND)

                    "then the `exists` function should return false" {
                        list.exists { it == THIRD } shouldBe false
                    }
                }
            }
        }
    }

    internal companion object {
        private const val FIRST = 1
        private const val SECOND = 2
        private const val THIRD = 3
    }
}
