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

package io.github.airflux.serialization.parser.json

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class StackTest : FreeSpec() {

    init {

        "The Stack type" - {

            "when the stack is empty" - {
                val stack = Stack<Int>()

                "then the `isEmpty` function should return true" {
                    stack.isEmpty() shouldBe true
                }

                "then the `pop` function should throw an exception" {
                    shouldThrow<IllegalStateException> {
                        stack.pop()
                    }
                }

                "then the `peek` function should throw an exception" {
                    shouldThrow<IllegalStateException> {
                        stack.peek()
                    }
                }
            }

            "when the stack is not empty" - {

                "then the `isEmpty` function should return false" {
                    val stack = Stack<Int>().apply {
                        push(FIRST_ELEMENT)
                        push(SECOND_ELEMENT)
                        push(THIRD_ELEMENT)
                    }
                    stack.isEmpty() shouldBe false
                }

                "then the `pop` function should extract and return the last element" {
                    val stack = Stack<Int>().apply {
                        push(FIRST_ELEMENT)
                        push(SECOND_ELEMENT)
                        push(THIRD_ELEMENT)
                    }

                    stack.pop() shouldBe THIRD_ELEMENT
                    stack.pop() shouldBe SECOND_ELEMENT
                    stack.pop() shouldBe FIRST_ELEMENT
                }

                "then the `peek` function should return the last element without retrieving it" {
                    val stack = Stack<Int>().apply {
                        push(FIRST_ELEMENT)
                        push(SECOND_ELEMENT)
                        push(THIRD_ELEMENT)
                    }

                    stack.peek() shouldBe THIRD_ELEMENT
                    stack.peek() shouldBe THIRD_ELEMENT
                    stack.peek() shouldBe THIRD_ELEMENT
                }
            }
        }
    }

    private companion object {
        private const val FIRST_ELEMENT = 1
        private const val SECOND_ELEMENT = 2
        private const val THIRD_ELEMENT = 3
    }
}
