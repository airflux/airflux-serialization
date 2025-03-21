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

import io.github.airflux.serialization.parser.json.CharBuffer

@Suppress("TooManyFunctions")
internal class MutableCharBuffer(capacity: Int = DEFAULT_CAPACITY) : CharBuffer {
    private var buffer: CharArray = CharArray(capacity)
    private var position = EMPTY_POSITION

    val capacity: Int
        get() = buffer.size

    override val length: Int
        get() = position

    override fun get(index: Int): Char {
        require(index >= 0 && index < position) { "Invalid the `index` parameter." }
        return buffer[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        checkRange(length = position, startIndex = startIndex, endIndex = endIndex)
        return String(buffer, startIndex, endIndex - startIndex)
    }

    override fun buildString(): String = buildString(0, position)

    override fun buildString(startIndex: Int, endIndex: Int): String {
        checkRange(length = position, startIndex = startIndex, endIndex = endIndex)
        return String(buffer, startIndex, endIndex - startIndex)
    }

    fun clear() {
        position = EMPTY_POSITION
    }

    fun append(char: Char) {
        checkAndResize(1)
        buffer[position++] = char
    }

    fun append(text: String, startIndex: Int = 0, endIndex: Int = text.length) {
        checkRange(startIndex = startIndex, endIndex = endIndex)
        checkAndResize(endIndex - startIndex)
        text.toCharArray(
            destination = buffer,
            destinationOffset = position,
            startIndex = startIndex,
            endIndex = endIndex
        )
        position += endIndex - startIndex
    }

    fun append(array: CharArray, startIndex: Int = 0, endIndex: Int = array.size) {
        checkRange(startIndex = startIndex, endIndex = endIndex)
        checkAndResize(endIndex - startIndex)
        array.copyInto(buffer, destinationOffset = position, startIndex = startIndex, endIndex = endIndex)
        position += endIndex - startIndex
    }

    override fun toCharArray(): CharArray {
        val result = CharArray(length)
        buffer.copyInto(result, startIndex = 0, endIndex = length)
        return result
    }

    private fun checkRange(length: Int, startIndex: Int, endIndex: Int) {
        checkRange(startIndex, endIndex)
        require(startIndex < length) {
            "Invalid the `startIndex` parameter. The parameter is greater than the `length`."
        }
        require(endIndex <= length) {
            "Invalid the `endIndex` parameter. The parameter is greater than the `length`."
        }
    }

    private fun checkRange(startIndex: Int, endIndex: Int) {
        require(startIndex >= 0) { "Invalid the `startIndex` parameter. The parameter is less than zero." }
        require(endIndex > 0) { "Invalid the `endIndex` parameter. The parameter is less than zero." }
        require(startIndex < endIndex) { "Invalid parameters. The `endIndex` is less or equal to `startIndex`." }
    }

    private fun checkAndResize(size: Int) {
        if (size + position > capacity)
            buffer = resize(calculateCapacity(size))
    }

    private fun resize(size: Int): CharArray = buffer.copyOf(size)

    private fun calculateCapacity(desiredSize: Int): Int {
        val scale = desiredSize.coerceIn(SCALE, desiredSize + SCALE)
        return capacity + scale
    }

    private companion object {
        private const val DEFAULT_CAPACITY = 16
        private const val EMPTY_POSITION = 0
        private const val SCALE = 16
    }
}
