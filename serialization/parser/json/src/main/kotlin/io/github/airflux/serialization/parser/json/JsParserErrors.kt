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

public sealed class JsParserErrors {
    public abstract val position: Int
    public abstract val line: Int
    public abstract val column: Int
    public abstract val description: String

    public data class UnexpectedEndOfInput(
        override val position: Int,
        override val line: Int,
        override val column: Int,
        private val reason: String? = null
    ) : JsParserErrors() {

        override val description: String = "Unexpected end-of-input" +
            (reason ?: NONE) +
            positionInfo(position = position, line = line, column = column)

        override fun toString(): String = description
    }

    public data class UnrecognizedToken(
        public val token: String,
        override val position: Int,
        override val line: Int,
        override val column: Int,
        private val reason: String? = null
    ) : JsParserErrors() {

        override val description: String = "Unrecognized token '$token'" +
            (reason ?: NONE) +
            positionInfo(position = position, line = line, column = column)

        override fun toString(): String = description
    }

    public data class UnexpectedCharacter(
        public val char: Char,
        override val position: Int,
        override val line: Int,
        override val column: Int,
        private val reason: String? = null
    ) : JsParserErrors() {

        override val description: String = "Unexpected character '$char'" +
            (reason ?: NONE) +
            positionInfo(position = position, line = line, column = column)

        override fun toString(): String = description
    }

    public data class UnrecognizedCharacterEscape(
        public val char: Char,
        override val position: Int,
        override val line: Int,
        override val column: Int
    ) : JsParserErrors() {

        override val description: String = "Unrecognized character '$char'" +
            positionInfo(position = position, line = line, column = column)

        override fun toString(): String = description
    }

    public companion object {
        private const val NONE = ""
        private fun positionInfo(position: Int, line: Int, column: Int): String =
            " at position $position [$line:$column]"
    }
}
