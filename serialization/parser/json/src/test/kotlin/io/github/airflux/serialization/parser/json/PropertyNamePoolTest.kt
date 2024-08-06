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

import io.github.airflux.serialization.parser.json.lexer.MutableCharBuffer
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class PropertyNamePoolTest : FreeSpec() {

    init {

        "The PropertyNamePool type" - {

            "when the instance is created" - {
                val pool = PropertyNamePool()

                "then size should be zero" {
                    pool.size shouldBe 0
                }
            }

            "when a single lexeme is added" - {
                val pool = PropertyNamePool()
                val value = pool.getOrPut(FIRST_LEXEME, 0, FIRST_LEXEME.length)

                "then size should be 1" {
                    pool.size shouldBe 1
                }

                "then the `getOrPut` function should return a string value for the added lexeme" {
                    value shouldBe FIRST_VALUE
                }
            }

            "when the same lexeme is added twice" - {
                val pool = PropertyNamePool()
                val firstResult = pool.getOrPut(FIRST_LEXEME, 0, FIRST_LEXEME.length)
                val secondResult = pool.getOrPut(FIRST_LEXEME, 0, FIRST_LEXEME.length)

                "then the size should be equal to the number of unique values added" {
                    pool.size shouldBe 1
                }

                "then the `getOrPut` function should return the same string value instance both times" {
                    firstResult shouldBeSameInstanceAs secondResult
                }
            }

            "when the lexemes same start but different length are added" - {

                "when first added lexeme is shorter" - {
                    val pool = PropertyNamePool()
                    val firstResult = pool.getOrPut(FIRST_LEXEME, 0, FIRST_LEXEME.length)
                    val secondResult = pool.getOrPut(TEN_LEXEME, 0, TEN_LEXEME.length)

                    "then the size should be equal to the number of unique values added" {
                        pool.size shouldBe 2
                    }

                    "then the `getOrPut` function should return a string value for each added lexeme" {
                        firstResult shouldBe FIRST_VALUE
                        secondResult shouldBe TEN_VALUE
                    }
                }

                "when first added lexeme is longer" - {
                    val pool = PropertyNamePool()
                    val firstResult = pool.getOrPut(TEN_LEXEME, 0, TEN_LEXEME.length)
                    val secondResult = pool.getOrPut(FIRST_LEXEME, 0, FIRST_LEXEME.length)

                    "then the size should be equal to the number of unique values added" {
                        pool.size shouldBe 2
                    }

                    "then the `getOrPut` function should return a string value for each added lexeme" {
                        firstResult shouldBe TEN_VALUE
                        secondResult shouldBe FIRST_VALUE
                    }
                }
            }

            "when multiple unique lexeme are added" - {
                val pool = PropertyNamePool()
                val firstResult = pool.getOrPut(FIRST_LEXEME, 0, FIRST_LEXEME.length)
                val secondResult = pool.getOrPut(SECOND_LEXEME, 0, SECOND_LEXEME.length)
                val thirdResult = pool.getOrPut(THIRD_LEXEME, 0, THIRD_LEXEME.length)
                val fourResult = pool.getOrPut(FOUR_LEXEME, 0, FOUR_LEXEME.length)
                val fiveResult = pool.getOrPut(FIVE_LEXEME, 0, FIVE_LEXEME.length)
                val sixResult = pool.getOrPut(SIX_LEXEME, 0, SIX_LEXEME.length)
                val sevenResult = pool.getOrPut(SEVEN_LEXEME, 0, SEVEN_LEXEME.length)
                val eightResult = pool.getOrPut(EIGHT_LEXEME, 0, EIGHT_LEXEME.length)
                val nineResult = pool.getOrPut(NINE_LEXEME, 0, NINE_LEXEME.length)
                val tenResult = pool.getOrPut(TEN_LEXEME, 0, TEN_LEXEME.length)

                "then the size should be equal to the number of unique values added" {
                    pool.size shouldBe 10
                }

                "then the `getOrPut` function should return a string value for each added lexeme" {
                    firstResult shouldBe FIRST_VALUE
                    secondResult shouldBe SECOND_VALUE
                    thirdResult shouldBe THIRD_VALUE
                    fourResult shouldBe FOUR_VALUE
                    fiveResult shouldBe FIVE_VALUE
                    sixResult shouldBe SIX_VALUE
                    sevenResult shouldBe SEVEN_VALUE
                    eightResult shouldBe EIGHT_VALUE
                    nineResult shouldBe NINE_VALUE
                    tenResult shouldBe TEN_VALUE
                }
            }
        }
    }

    private companion object {
        private const val FIRST_VALUE = "1"
        private const val SECOND_VALUE = "2"
        private const val THIRD_VALUE = "3"
        private const val FOUR_VALUE = "4"
        private const val FIVE_VALUE = "5"
        private const val SIX_VALUE = "6"
        private const val SEVEN_VALUE = "7"
        private const val EIGHT_VALUE = "8"
        private const val NINE_VALUE = "9"
        private const val TEN_VALUE = "10"

        private val FIRST_LEXEME = MutableCharBuffer().apply { append(FIRST_VALUE) }
        private val SECOND_LEXEME = MutableCharBuffer().apply { append(SECOND_VALUE) }
        private val THIRD_LEXEME = MutableCharBuffer().apply { append(THIRD_VALUE) }
        private val FOUR_LEXEME = MutableCharBuffer().apply { append(FOUR_VALUE) }
        private val FIVE_LEXEME = MutableCharBuffer().apply { append(FIVE_VALUE) }
        private val SIX_LEXEME = MutableCharBuffer().apply { append(SIX_VALUE) }
        private val SEVEN_LEXEME = MutableCharBuffer().apply { append(SEVEN_VALUE) }
        private val EIGHT_LEXEME = MutableCharBuffer().apply { append(EIGHT_VALUE) }
        private val NINE_LEXEME = MutableCharBuffer().apply { append(NINE_VALUE) }
        private val TEN_LEXEME = MutableCharBuffer().apply { append(TEN_VALUE) }
    }
}
