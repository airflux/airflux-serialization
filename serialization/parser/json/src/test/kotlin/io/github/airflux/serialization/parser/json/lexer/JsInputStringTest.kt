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

package io.github.airflux.serialization.parser.json.lexer

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsInputStringTest : FreeSpec() {

    init {

        "The JsInputString type" - {

            "when the input contains some char" - {
                val input = "a"

                "when there was no attempt to read a char" - {
                    val source = JsInputString(input)

                    "then isEOF should be false" {
                        source.isEOF shouldBe false
                    }

                    "then position should be -1" {
                        source.position shouldBe -1
                    }

                    "then line should be -1" {
                        source.line shouldBe -1
                    }

                    "then column should be -1" {
                        source.column shouldBe -1
                    }
                }

                "when there was an attempt to read a char" - {
                    val source = JsInputString(input)
                    val char = source.nextChar()

                    "then the variable should contain the character read" {
                        char shouldBe 'a'
                    }

                    "then isEOF should be false" {
                        source.isEOF shouldBe false
                    }

                    "then position should be 0" {
                        source.position shouldBe 0
                    }

                    "then line should be 1" {
                        source.line shouldBe 1
                    }

                    "then column should be 1" {
                        source.column shouldBe 1
                    }
                }
            }

            "when the input contains multiple chars" - {
                val input = "ab"

                "when there was no attempt to read a char" - {
                    val source = JsInputString(input)

                    "then isEOF should be false" {
                        source.isEOF shouldBe false
                    }

                    "then position should be -1" {
                        source.position shouldBe -1
                    }

                    "then line should be -1" {
                        source.line shouldBe -1
                    }

                    "then column should be -1" {
                        source.column shouldBe -1
                    }
                }

                "when there was an attempt to read a char" - {
                    val source = JsInputString(input)
                    val elements = source.scan()

                    "then the list of read elements should contain the expected elements" {
                        elements shouldContainExactly listOf(
                            Element(char = 'a', position = 0, line = 1, column = 1, isEOF = false),
                            Element(char = 'b', position = 1, line = 1, column = 2, isEOF = false),
                            Element(char = null, position = 2, line = 1, column = 3, isEOF = true),
                        )
                    }
                }
            }

            "when the input contains only newline char" - {
                val input = '\n'.toString()
                val source = JsInputString(input)

                "when there was no attempt to read a char" - {

                    "then isEOF should be false" {
                        source.isEOF shouldBe false
                    }

                    "then position should be -1" {
                        source.position shouldBe -1
                    }

                    "then line should be -1" {
                        source.line shouldBe -1
                    }

                    "then column should be -1" {
                        source.column shouldBe -1
                    }
                }

                "when there was an attempt to read a char" - {
                    val char = source.nextChar()

                    "then the variable should contain the character read" {
                        char shouldBe '\n'
                    }

                    "then isEOF should be false" {
                        source.isEOF shouldBe false
                    }

                    "then position should be 0" {
                        source.position shouldBe 0
                    }

                    "then line should be 2" {
                        source.line shouldBe 2
                    }

                    "then column should be 1" {
                        source.column shouldBe 1
                    }
                }
            }

            "when the input contains multiple chars separated by a newline char" - {
                val input = "a" + '\n' + "b"

                "when there was no attempt to read a char" - {
                    val source = JsInputString(input)

                    "then isEOF should be false" {
                        source.isEOF shouldBe false
                    }

                    "then position should be -1" {
                        source.position shouldBe -1
                    }

                    "then line should be -1" {
                        source.line shouldBe -1
                    }

                    "then column should be -1" {
                        source.column shouldBe -1
                    }
                }

                "when there was an attempt to read a char" - {
                    val source = JsInputString(input)
                    val elements = source.scan()

                    "then the list of read elements should contain the expected elements" {
                        elements shouldContainExactly listOf(
                            Element(char = 'a', position = 0, line = 1, column = 1, isEOF = false),
                            Element(char = '\n', position = 1, line = 2, column = 1, isEOF = false),
                            Element(char = 'b', position = 2, line = 2, column = 2, isEOF = false),
                            Element(char = null, position = 3, line = 2, column = 3, isEOF = true),
                        )
                    }
                }
            }

            "when the input is empty" - {
                val input = ""

                "when there was no attempt to read a char" - {
                    val source = JsInputString(input)

                    "then isEOF should be true" {
                        source.isEOF shouldBe true
                    }

                    "then position should be -1" {
                        source.position shouldBe -1
                    }

                    "then line should be -1" {
                        source.line shouldBe -1
                    }

                    "then column should be -1" {
                        source.column shouldBe -1
                    }
                }

                "when there was an attempt to read a char" - {
                    val source = JsInputString(input)
                    val char = source.nextChar()

                    "then char should be null" {
                        char.shouldBeNull()
                    }

                    "then isEOF should be true" {
                        source.isEOF shouldBe true
                    }

                    "then position should be -1" {
                        source.position shouldBe -1
                    }

                    "then line should be -1" {
                        source.line shouldBe -1
                    }

                    "then column should be -1" {
                        source.column shouldBe -1
                    }
                }
            }
        }
    }

    private fun JsInputString.scan(): List<Element> {
        val elements = mutableListOf<Element>()
        while (true) {
            val char = nextChar()
            elements.add(Element(char, position = position, line = line, column = column, isEOF))
            if (char == null) break
        }
        return elements
    }

    private data class Element(val char: Char?, val position: Int, val line: Int, val column: Int, val isEOF: Boolean)
}
