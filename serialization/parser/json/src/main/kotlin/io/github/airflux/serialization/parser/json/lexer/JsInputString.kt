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

public class JsInputString(private val input: String) : JsSourceInput {

    override var position: Int = -1
        private set

    override var line: Int = NONE
        private set

    override var column: Int = NONE
        private set

    override var isEOF: Boolean = input.isEmpty()
        private set

    override fun nextChar(): Char? {
        if (isEOF) return null

        position++
        if (position >= input.length) {
            isEOF = true
            column++
            return null
        }

        val char = input[position]

        if (line == NONE) line = INIT_POSITION_LINE
        if (char != NEW_LINE_CHAR) {
            if (column == NONE)
                column = INIT_POSITION_COLUMN
            else
                column++
        } else {
            line++
            column = INIT_POSITION_COLUMN
        }
        return char
    }

    private companion object {
        private const val NONE = 0
        private const val INIT_POSITION_COLUMN = 1
        private const val INIT_POSITION_LINE = 1

        private const val NEW_LINE_CHAR = '\n'
    }
}
