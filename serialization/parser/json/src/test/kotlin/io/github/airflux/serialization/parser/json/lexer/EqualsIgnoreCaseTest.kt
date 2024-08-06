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
import io.kotest.matchers.shouldBe

internal class EqualsIgnoreCaseTest : FreeSpec() {

    init {
        "The extension function `equalsIgnoreCase`" - {

            "when the length of the strings is different" - {
                val text = ABC
                val other = AB

                "then the function should return false" {
                    text.equalsIgnoreCase(other) shouldBe false
                }
            }

            "when length of the strings is the same" - {

                "when the strings are empty" - {
                    val text = ""
                    val other = ""

                    "then the function should return true" {
                        text.equalsIgnoreCase(other) shouldBe true
                    }
                }

                "when the strings are not empty" - {

                    "when the strings are equal" - {
                        val text = ABC
                        val other = ABC_UPPER

                        "then the function should return true" {
                            text.equalsIgnoreCase(other) shouldBe true
                        }
                    }

                    "when the strings are not equal" - {
                        val text = ABC
                        val other = ABD

                        "then the function should return false" {
                            text.equalsIgnoreCase(other) shouldBe false
                        }
                    }
                }
            }

            "when the strings are the same" - {
                val text = ABC
                val other = ABC

                "then the function should return true" {
                    text.equalsIgnoreCase(other) shouldBe true
                }
            }
        }
    }

    private companion object {
        private const val AB = "ab"
        private const val ABC = "abc"
        private const val ABC_UPPER = "ABC"
        private const val ABD = "abd"
    }
}
