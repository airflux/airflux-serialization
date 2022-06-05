package io.github.airflux.core.reader.context

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class JsReaderContextTest : FreeSpec() {

    companion object {
        private val userContext = UserContext()
        private val orderContext = OrderContext()
    }

    init {

        "The JsReaderContext" - {

            "when calling the constructor" - {

                "without parameters" - {
                    val empty = JsReaderContext()

                    "context should be is empty" {
                        empty.isEmpty shouldBe true
                    }
                }

                "with one parameters" - {
                    val one = JsReaderContext(userContext)

                    "context should not be is empty" {
                        one.isEmpty shouldBe false
                    }

                    "context should be contains passed parameter" {
                        val result = one.contains(UserContext)
                        result shouldBe true
                    }
                }

                "with the collection of the parameters" - {
                    val elements = listOf(userContext, orderContext)
                    val one = JsReaderContext(elements)

                    "context should not be is empty" {
                        one.isEmpty shouldBe false
                    }

                    "context should be contains passed parameters" - {
                        withData(nameFn = { "$it" }, elements) { element ->
                            val result = one.contains(element.key)
                            result shouldBe true
                        }
                    }
                }
            }

            "when context is empty" - {
                val empty = JsReaderContext()

                "property isEmpty should return true" - {
                    empty.isEmpty shouldBe true
                }

                "property isNotEmpty should return false" - {
                    empty.isNotEmpty shouldBe false
                }

                "the function getOrNull should return null by any key" {
                    val result = empty.getOrNull(UserContext)
                    result.shouldBeNull()
                }

                "the function getValue should throw exception by any key" {
                    val exception = shouldThrow<NoSuchElementException> {
                        empty.getValue(OrderContext)
                    }
                    exception.message shouldBe "Key '${OrderContext.name}' is missing in the JsReaderContext."
                }

                "the function contains should return false by any key" {
                    val result = empty.contains(UserContext)
                    result shouldBe false
                }

                "when calling the function plus" - {

                    "with one parameter" - {
                        val afterAdded = empty + UserContext()

                        "context should be contains passed parameter" {
                            val result = afterAdded.contains(UserContext)
                            result shouldBe true
                        }
                    }

                    "with the collection of the parameters" - {
                        val addingContext = listOf(userContext)
                        val afterAdded = empty + addingContext

                        "context should be contains passed parameters" - {
                            withData(nameFn = { "$it" }, addingContext) { element ->
                                val result = afterAdded.contains(element.key)
                                result shouldBe true
                            }
                        }
                    }
                }
            }

            "when context is not empty" - {
                val one = JsReaderContext(userContext)

                "property isEmpty should return false" - {
                    one.isEmpty shouldBe false
                }

                "property isNotEmpty should return true" - {
                    one.isNotEmpty shouldBe true
                }

                "the function contains should return true if data by the key contains in the context" {
                    val result = one.contains(UserContext)
                    result shouldBe true
                }

                "the function getOrNull should return value if data by the key contains in the context" {
                    val result = one.getOrNull(UserContext)
                    result.shouldNotBeNull()
                    result shouldBe userContext
                }

                "the function getValue should return value if data by the key contains in the context" {
                    val result = one.getValue(UserContext)
                    result shouldBe userContext
                }

                "the function contains should return false if data by the key does not contain in the context" {
                    val result = one.contains(OrderContext)
                    result shouldBe false
                }

                "the function getOrNull should return null if data by the key does not contain in the context" {
                    val result = one.getOrNull(OrderContext)
                    result.shouldBeNull()
                }

                "the function getValue should throw exception if data by the key does not contain in the context" {
                    val exception = shouldThrow<NoSuchElementException> {
                        one.getValue(OrderContext)
                    }
                    exception.message shouldBe "Key '${OrderContext.name}' is missing in the JsReaderContext."
                }

                "when calling the function plus" - {

                    "with one parameter" - {
                        val addingContext = OrderContext()
                        val afterAdded = one + addingContext

                        "context should contain old data" {
                            val result = afterAdded.contains(UserContext)
                            result shouldBe true
                        }

                        "context should be contains passed parameter" {
                            val result = afterAdded.contains(addingContext.key)
                            result shouldBe true
                        }
                    }

                    "with the collection of the parameters" - {
                        val addingContext = listOf(orderContext)
                        val afterAdded = one + addingContext

                        "context should contain old data" {
                            val result = afterAdded.contains(UserContext)
                            result shouldBe true
                        }

                        "context should be contains passed parameters" - {
                            withData(nameFn = { "$it" }, addingContext) { element ->
                                val result = afterAdded.contains(element.key)
                                result shouldBe true
                            }
                        }
                    }
                }
            }

            "Element" - {
                "the function plus should return the collection of the elements" {
                    val result = UserContext() + OrderContext()

                    result shouldContainExactly listOf(UserContext(), OrderContext())
                }
            }
        }
    }

    class UserContext : JsReaderAbstractContextElement<UserContext>(Key) {
        companion object Key : JsReaderContext.Key<UserContext> {
            override val name: String = "UserContext"
        }

        override fun toString(): String = name
        override fun equals(other: Any?): Boolean = this === other || other is UserContext
        override fun hashCode(): Int = Key.hashCode()
    }

    class OrderContext : JsReaderAbstractContextElement<OrderContext>(Key) {
        companion object Key : JsReaderContext.Key<OrderContext> {
            override val name: String = "OrderContext"
        }

        override fun toString(): String = name
        override fun equals(other: Any?): Boolean = this === other || other is OrderContext
        override fun hashCode(): Int = UserContext.hashCode()
    }
}
