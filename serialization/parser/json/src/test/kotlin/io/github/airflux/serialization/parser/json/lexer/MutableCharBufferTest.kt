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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

internal class MutableCharBufferTest : FreeSpec() {

    init {
        "The MutableCharBuffer type" - {

            "when instance of the type was created" - {
                val buffer = MutableCharBuffer(MAX_CAPACITY)

                "then the `capacity` property should be equal to the initial capacity" {
                    buffer.capacity shouldBe MAX_CAPACITY
                }

                "then the `length` property should be zero" {
                    buffer.length shouldBe 0
                }
            }

            "when the buffer is empty" - {

                "then the `length` property should be zero" {
                    val buffer = MutableCharBuffer()
                    buffer.length shouldBe 0
                }

                "when the `get` function was called" - {
                    val buffer = MutableCharBuffer()

                    withData(
                        nameFn = { "then the function should throw an exception for index $it" },
                        listOf(-1, 0, 1)
                    ) { index ->
                        shouldThrow<IllegalArgumentException> {
                            buffer[index]
                        }
                    }
                }

                "when the `subSequence` function was called" - {
                    val buffer = MutableCharBuffer()

                    withData(
                        nameFn = { "then the function should throw an exception for range ${it.first}:${it.second}" },
                        listOf(0 to 1)
                    ) { (startIndex, endIndex) ->
                        shouldThrow<IllegalArgumentException> {
                            buffer.subSequence(startIndex, endIndex)
                        }
                    }
                }

                "when the `buildString` function was called" - {
                    val buffer = MutableCharBuffer()

                    withData(
                        nameFn = { "then the function should throw an exception for range ${it.first}:${it.second}" },
                        listOf(0 to 1)
                    ) { (startIndex, endIndex) ->
                        shouldThrow<IllegalArgumentException> {
                            buffer.buildString(startIndex, endIndex)
                        }
                    }
                }

                "when the `clear` function was called" - {
                    val buffer = MutableCharBuffer()
                    buffer.clear()

                    "then the `length` property should be zero" {
                        buffer.length shouldBe 0
                    }

                    "when the `get` function was called" - {
                        withData(
                            nameFn = { "then the function should throw an exception for index $it" },
                            listOf(-1, 0, 1)
                        ) { index ->
                            shouldThrow<IllegalArgumentException> {
                                buffer[index]
                            }
                        }
                    }

                    "when the `subSequence` function was called" - {

                        withData(
                            nameFn = { "then the function should throw an exception for range ${it.first}:${it.second}" },
                            listOf(0 to 1)
                        ) { (startIndex, endIndex) ->
                            shouldThrow<IllegalArgumentException> {
                                buffer.subSequence(startIndex, endIndex)
                            }
                        }
                    }

                    "when the `buildString` function was called" - {

                        withData(
                            nameFn = { "then the function should throw an exception for range ${it.first}:${it.second}" },
                            listOf(0 to 1)
                        ) { (startIndex, endIndex) ->
                            shouldThrow<IllegalArgumentException> {
                                buffer.buildString(startIndex, endIndex)
                            }
                        }
                    }
                }

                "when the `toCharArray` function was called" - {
                    val buffer = MutableCharBuffer()
                    val result = buffer.toCharArray()

                    "then the function should return an empty array" {
                        result.size shouldBe 0
                    }
                }
            }

            "when the buffer is not empty" - {

                "then the `length` property should be equal to the number of chars added" {
                    val buffer = MutableCharBuffer(MAX_CAPACITY).apply {
                        CHARS.forEach { char -> append(char) }
                    }

                    buffer.length shouldBe CHARS.size
                }

                "when the `get` function was called" - {
                    val buffer = MutableCharBuffer(MAX_CAPACITY).apply {
                        CHARS.forEach { char -> append(char) }
                    }

                    withData(
                        nameFn = { "then the function should return a char for valid index $it" },
                        CHARS.indices
                    ) { index ->
                        buffer[index] shouldBe STR[index]
                    }

                    withData(
                        nameFn = { "then the function should throw an exception for invalid index $it" },
                        listOf(-1, CHARS.size + 1)
                    ) { index ->
                        shouldThrow<IllegalArgumentException> {
                            buffer[index]
                        }
                    }
                }

                "when the `subSequence` function was called" - {
                    val buffer = MutableCharBuffer(MAX_CAPACITY).apply {
                        CHARS.forEach { char -> append(char) }
                    }

                    withData(
                        nameFn = { "then the function should a string for valid parameters (startIndex: ${it.first}, endIndex: ${it.second})" },
                        listOf(
                            0 to 1,
                            0 to 2,
                            0 to 3,
                            1 to 2,
                            1 to 3,
                            2 to 3,
                        )
                    ) { (startIndex, endIndex) ->
                        buffer.subSequence(startIndex, endIndex) shouldBe STR.substring(startIndex, endIndex)
                    }

                    withData(
                        nameFn = { "then the function should throw an exception for invalid parameters (startIndex: ${it.first}, endIndex: ${it.second})" },
                        listOf(
                            -1 to 0,
                            0 to 4,
                            2 to 1,
                            1 to -1
                        )
                    ) { (startIndex, endIndex) ->
                        shouldThrow<IllegalArgumentException> {
                            buffer.buildString(startIndex, endIndex)
                        }
                    }
                }

                "when the `buildString` function was called" - {
                    val buffer = MutableCharBuffer(MAX_CAPACITY).apply {
                        CHARS.forEach { char -> append(char) }
                    }

                    withData(
                        nameFn = { "then the function should a string for valid parameters (startIndex: ${it.first}, endIndex: ${it.second})" },
                        listOf(
                            0 to 1,
                            0 to 2,
                            0 to 3,
                            1 to 2,
                            1 to 3,
                            2 to 3,
                        )
                    ) { (startIndex, endIndex) ->
                        buffer.buildString(startIndex, endIndex) shouldBe STR.substring(startIndex, endIndex)
                    }

                    withData(
                        nameFn = { "then the function should throw an exception for invalid parameters (startIndex: ${it.first}, endIndex: ${it.second})" },
                        listOf(
                            -1 to 0,
                            0 to 4,
                            2 to 1,
                            1 to -1
                        )
                    ) { (startIndex, endIndex) ->
                        shouldThrow<IllegalArgumentException> {
                            buffer.buildString(startIndex, endIndex)
                        }
                    }
                }

                "when the `clear` function was called" - {
                    val buffer = MutableCharBuffer(MAX_CAPACITY).apply {
                        append(STR)
                    }
                    buffer.clear()

                    "then the `length` property should be zero" {
                        buffer.length shouldBe 0
                    }

                    "when the `get` function was called" - {
                        withData(
                            nameFn = { "then the function should throw an exception for index $it" },
                            listOf(-1, 0, 1)
                        ) { index ->
                            shouldThrow<IllegalArgumentException> {
                                buffer[index]
                            }
                        }
                    }

                    "when the `subSequence` function was called" - {

                        withData(
                            nameFn = { "then the function should throw an exception for range ${it.first}:${it.second}" },
                            listOf(0 to 1)
                        ) { (startIndex, endIndex) ->
                            shouldThrow<IllegalArgumentException> {
                                buffer.subSequence(startIndex, endIndex)
                            }
                        }
                    }

                    "when the `buildString` function was called" - {

                        withData(
                            nameFn = { "then the function should throw an exception for range ${it.first}:${it.second}" },
                            listOf(0 to 1)
                        ) { (startIndex, endIndex) ->
                            shouldThrow<IllegalArgumentException> {
                                buffer.buildString(startIndex, endIndex)
                            }
                        }
                    }
                }

                "when the `toCharArray` function was called" - {
                    val buffer = MutableCharBuffer().apply { append(STR) }
                    val result = buffer.toCharArray()

                    "then the function should return an array with all added chars" {
                        result shouldBe STR.toCharArray()
                    }
                }
            }

            "when the buffer capacity is sufficient to add chars" - {
                val capacity = CHARS.size.coerceAtLeast(STR.length)

                "when some chars are added" - {
                    val buffer = MutableCharBuffer(capacity).apply {
                        CHARS.forEach { char -> append(char) }
                    }

                    "then the capacity should be not changed" {
                        buffer.capacity shouldBe capacity
                    }

                    "then the `length` property should be equal to the number of chars added" {
                        buffer.length shouldBe CHARS.size
                    }

                    "then the buffer should contain all added chars" - {
                        withData(
                            nameFn = { "the char `${it.second}` at the index `${it.first}`" },
                            CHARS.mapIndexed { index, char -> index to char }
                        ) { (index, char) ->
                            buffer[index] shouldBe char
                        }
                    }
                }

                "when some string is added" - {
                    val buffer = MutableCharBuffer(capacity).apply {
                        append(STR)
                    }

                    "then the capacity should be not changed" {
                        buffer.capacity shouldBe capacity
                    }

                    "then the `length` property should be equal to the number of chars added" {
                        buffer.length shouldBe STR.length
                    }

                    "then the buffer should contain all added chars" - {
                        withData(
                            nameFn = { "the char `${it.second}` at the index `${it.first}`" },
                            STR.mapIndexed { index, char -> index to char }
                        ) { (index, char) ->
                            buffer[index] shouldBe char
                        }
                    }
                }
            }

            "when the buffer capacity is not sufficient to add chars" - {
                val capacity = MIN_CAPACITY

                "when some chars are added" - {
                    val buffer = MutableCharBuffer(capacity).apply {
                        CHARS.forEach { char -> append(char) }
                    }

                    "then the capacity should be changed" {
                        buffer.capacity shouldBeGreaterThan capacity
                    }

                    "then the `length` property should be equal to the number of chars added" {
                        buffer.length shouldBe CHARS.size
                    }

                    "then the buffer should contain all added chars" - {
                        withData(
                            nameFn = { "the char `${it.second}` at the index `${it.first}`" },
                            CHARS.mapIndexed { index, char -> index to char }
                        ) { (index, char) ->
                            buffer[index] shouldBe char
                        }
                    }
                }

                "when some string is added" - {
                    val buffer = MutableCharBuffer(capacity).apply {
                        append(STR)
                    }

                    "then the capacity should be changed" {
                        buffer.capacity shouldBeGreaterThan capacity
                    }

                    "then the `length` property should be equal to the number of chars added" {
                        buffer.length shouldBe STR.length
                    }

                    "then the buffer should contain all added chars" - {
                        withData(
                            nameFn = { "the char `${it.second}` at the index `${it.first}`" },
                            STR.mapIndexed { index, char -> index to char }
                        ) { (index, char) ->
                            buffer[index] shouldBe char
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private const val MIN_CAPACITY = 1
        private const val MAX_CAPACITY = 16

        private const val FIRST_CHAR = 'a'
        private const val SECOND_CHAR = 'b'
        private const val THIRD_CHAR = 'c'

        private val CHARS = listOf(FIRST_CHAR, SECOND_CHAR, THIRD_CHAR)
        private val STR = CHARS.joinToString(separator = "", prefix = "", postfix = "")
    }
}
