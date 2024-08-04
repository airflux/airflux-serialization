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

package io.github.airflux.serialization.core.context

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class JsContextTest : FreeSpec() {

    companion object {
        private const val FIRST_USER_ID = "9a222dba-a59b-4df8-b118-8c7bc68db501"
        private const val SECOND_USER_ID = "b0fdc91b-ef91-49d5-bed3-20f4bb47e93d"
    }

    init {

        "The JsContext" - {

            "when the context does not contain any elements" - {
                val context = JsContext.Empty

                "then the `isEmpty` property should return true" {
                    context.isEmpty shouldBe true
                }

                "then the `isNotEmpty` property should return false" {
                    context.isNotEmpty shouldBe false
                }

                "then the `get` function should return null by any key" {
                    val result = context[UserContext]
                    result.shouldBeNull()
                }

                "then the `contains` function should return false by any key" {
                    val result = UserContext in context
                    result shouldBe false
                }

                "then the `fold` function should return initial value" {
                    val result = context.fold(mutableListOf<String>()) { acc, elem ->
                        acc.apply { add(elem.toString()) }
                    }

                    result shouldBe emptyList()
                }
            }

            "when the context contains some elements" - {
                val context = JsContext.Empty + UserContext(FIRST_USER_ID) + OrderContext()

                "then the `isEmpty` property should return false" {
                    context.isEmpty shouldBe false
                }

                "then the `isNotEmpty` property should return true" {
                    context.isNotEmpty shouldBe true
                }

                "then the `get` function should return the value for every element in the context" {
                    context[UserContext].shouldNotBeNull()
                    context[OrderContext].shouldNotBeNull()
                }

                "then the `get` function should return null for any element not contained in the context" {
                    context[BillContext].shouldBeNull()
                }

                "then the `contains` function should return true for every element in the context" {
                    val result = UserContext in context
                    result shouldBe true
                }

                "then the `contains` function should return false for any element not contained in the context" {
                    val result = BillContext in context
                    result shouldBe false
                }

                "then the `fold` function should return the list of names of all elements" {
                    val result = context.fold(mutableListOf<String>()) { acc, elem ->
                        acc.apply { add(elem.toString()) }
                    }

                    result shouldBe listOf(OrderContext.NAME, UserContext.NAME)
                }
            }

            "when an element is a context" - {
                val context: JsContext = UserContext(FIRST_USER_ID)

                "then the `isEmpty` property should return false" {
                    context.isEmpty shouldBe false
                }

                "then the `isNotEmpty` property should return true" {
                    context.isNotEmpty shouldBe true
                }

                "then the `get` function should return the value if the key of the element match" {
                    context[UserContext].shouldNotBeNull()
                }

                "then the `get` function should return null if the element's key does not match" {
                    context[OrderContext].shouldBeNull()
                }

                "then the `contains` function should return true if the key of the element match" {
                    val result = UserContext in context
                    result shouldBe true
                }

                "then the `contains` function should return false if the element's key does not match" {
                    val result = OrderContext in context
                    result shouldBe false
                }

                "then the `fold` function should return the name of the element" {
                    val result = context.fold(mutableListOf<String>()) { acc, elem ->
                        acc.apply { add(elem.toString()) }
                    }

                    result shouldBe listOf(UserContext.NAME)
                }

                "when a new element is added to the context" - {
                    val newContext = context + OrderContext()

                    "then the `isEmpty` property should return false" {
                        newContext.isEmpty shouldBe false
                    }

                    "then the `isNotEmpty` property should return true" {
                        newContext.isNotEmpty shouldBe true
                    }

                    "then the `get` function should return the value for every element in the context" {
                        newContext[UserContext].shouldNotBeNull()
                        newContext[OrderContext].shouldNotBeNull()
                    }

                    "then the `get` function should return null for any element not contained in the context" {
                        newContext[BillContext].shouldBeNull()
                    }

                    "then the `contains` function should return true for every element in the context" {
                        val result = UserContext in newContext
                        result shouldBe true
                    }

                    "then the `contains` function should return false for any element not contained in the context" {
                        val result = BillContext in newContext
                        result shouldBe false
                    }

                    "then the `fold` function should return the list of names of all elements" {
                        val result = newContext.fold(mutableListOf<String>()) { acc, elem ->
                            acc.apply { add(elem.toString()) }
                        }

                        result shouldBe listOf(OrderContext.NAME, UserContext.NAME)
                    }
                }
            }

            "when a duplicate element is added to the context" - {
                val contextWithDuplicate = JsContext.Empty + UserContext(FIRST_USER_ID) + UserContext(SECOND_USER_ID)

                "then the `get` function should return the value of the last added element" {
                    val result = contextWithDuplicate[UserContext]
                    result.shouldNotBeNull()
                    result.id shouldBe SECOND_USER_ID
                }
            }
        }
    }

    class UserContext(val id: String) : JsAbstractContextElement<UserContext>(UserContext) {
        override fun toString(): String = NAME
        override fun equals(other: Any?): Boolean = this === other || other is UserContext
        override fun hashCode(): Int = Key.hashCode()

        companion object Key : JsContext.Key<UserContext> {
            const val NAME = "UserContext"
        }
    }

    class OrderContext : JsAbstractContextElement<OrderContext>(OrderContext) {
        override fun toString(): String = NAME
        override fun equals(other: Any?): Boolean = this === other || other is OrderContext
        override fun hashCode(): Int = UserContext.hashCode()

        companion object Key : JsContext.Key<OrderContext> {
            const val NAME = "OrderContext"
        }
    }

    class BillContext : JsAbstractContextElement<BillContext>(BillContext) {
        override fun toString(): String = NAME
        override fun equals(other: Any?): Boolean = this === other || other is BillContext
        override fun hashCode(): Int = UserContext.hashCode()

        companion object Key : JsContext.Key<BillContext> {
            const val NAME = "BillContext"
        }
    }
}
