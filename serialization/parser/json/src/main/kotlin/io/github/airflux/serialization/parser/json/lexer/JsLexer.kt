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

import io.github.airflux.serialization.parser.json.JsParserErrors

@Suppress("TooManyFunctions")
internal class JsLexer(private val input: JsSourceInput) {
    private var peek: Char = WHITESPACE

    internal val position: Int
        get() = input.position

    internal val line: Int
        get() = input.line

    internal val column: Int
        get() = input.column

    internal val lexeme: JsLexeme = JsLexeme()
    internal var error: JsParserErrors? = null

    fun nextToken(): Terminal {
        peek = nextChar() ?: return Terminal.EOF
        return when (peek) {
            LEFT_BRACE_CHAR -> makeLeftBraceToken()
            RIGHT_BRACE_CHAR -> makeRightBraceToken()
            LEFT_BRACKET_CHAR -> makeLeftBracketToken()
            RIGHT_BRACKET_CHAR -> makeRightBracketToken()
            COLON_CHAR -> makeColonToken()
            COMMA_CHAR -> makeCommaToken()
            QUOTE_CHAR -> makeStringLiteralToken()
            else -> makeLiteralToken()
        }
    }

    private fun nextChar(): Char? {
        tailrec fun nextChar(char: Char?): Char? =
            if (char == null || input.isEOF)
                null
            else if (char.isSkip())
                nextChar(input.nextChar())
            else
                char
        return nextChar(peek)
    }

    private fun makeLeftBraceToken(): Terminal {
        makeOneCharLexeme()
        return Terminal.BRACE_LEFT
    }

    private fun makeRightBraceToken(): Terminal {
        makeOneCharLexeme()
        return Terminal.BRACE_RIGHT
    }

    private fun makeLeftBracketToken(): Terminal {
        makeOneCharLexeme()
        return Terminal.BRACKET_LEFT
    }

    private fun makeRightBracketToken(): Terminal {
        makeOneCharLexeme()
        return Terminal.BRACKET_RIGHT
    }

    private fun makeColonToken(): Terminal {
        makeOneCharLexeme()
        return Terminal.SEPARATOR_COLON
    }

    private fun makeCommaToken(): Terminal {
        makeOneCharLexeme()
        return Terminal.SEPARATOR_COMMA
    }

    private fun makeOneCharLexeme() {
        lexeme.init()
        peek = WHITESPACE
    }

    private fun makeStringLiteralToken(): Terminal {
        tailrec fun makeStringLiteralToken(escape: Boolean): Terminal {
            val char = input.nextChar()
            return when {
                char == null -> {
                    peek = WHITESPACE
                    Terminal.LITERAL
                }

                escape -> if (escaping(char))
                    makeStringLiteralToken(escape = false)
                else
                    unrecognizedCharacterEscape(char)

                char.isEscape() -> makeStringLiteralToken(escape = true)

                char.isQuote() -> {
                    lexeme += char
                    peek = WHITESPACE
                    Terminal.LITERAL
                }

                else -> {
                    peek = char
                    lexeme += peek
                    makeStringLiteralToken(escape = false)
                }
            }
        }

        lexeme.init()
        return makeStringLiteralToken(false)
    }

    private fun makeLiteralToken(): Terminal {
        tailrec fun makeLiteralToken(char: Char?): Terminal = when {
            char == null -> {
                peek = WHITESPACE
                Terminal.LITERAL
            }

            char == '\\' -> unexpectedCharacter(char)

            char.isFinal() -> {
                peek = char
                Terminal.LITERAL
            }

            else -> {
                peek = char
                lexeme += peek
                makeLiteralToken(input.nextChar())
            }
        }

        lexeme.initialize(position = input.position, line = input.line, column = input.column)
        return makeLiteralToken(peek)
    }

    private fun Char.isSkip(): Boolean = when (this) {
        WHITESPACE, TAB_CHAR, CARRIAGE_RETURN_CHAR, NEW_LINE_CHAR -> true
        else -> false
    }

    private fun Char.isEscape(): Boolean = this == ESCAPE_CHAR

    private fun escaping(char: Char): Boolean {
        when (char) {
            '"' -> lexeme += '"'
            '\\' -> lexeme += '\\'
            '/' -> lexeme += '/'
            'b' -> lexeme += '\b'
            'f' -> lexeme += "\\f"
            'n' -> lexeme += '\n'
            'r' -> lexeme += '\r'
            't' -> lexeme += '\t'
            'u' -> lexeme += "\\u"
            else -> return false
        }
        return true
    }

    private fun Char.isQuote(): Boolean = this == QUOTE_CHAR

    private fun Char.isFinal(): Boolean =
        when (this) {
            WHITESPACE, TAB_CHAR, CARRIAGE_RETURN_CHAR, NEW_LINE_CHAR,
            COMMA_CHAR, COLON_CHAR, LEFT_BRACE_CHAR, RIGHT_BRACE_CHAR,
            LEFT_BRACKET_CHAR, RIGHT_BRACKET_CHAR, QUOTE_CHAR -> true

            else -> false
        }

    private fun JsLexeme.init() {
        initialize(position = input.position, line = input.line, column = input.column)
        this += peek
    }

    private fun unrecognizedCharacterEscape(char: Char): Terminal {
        error = JsParserErrors.UnrecognizedCharacterEscape(
            char = char,
            position = input.position,
            line = input.line,
            column = input.column
        )
        return Terminal.ERROR
    }

    private fun unexpectedCharacter(char: Char): Terminal {
        error = JsParserErrors.UnexpectedCharacter(
            char = char,
            position = input.position,
            line = input.line,
            column = input.column
        )
        return Terminal.ERROR
    }

    private companion object {
        private const val WHITESPACE = ' '
        private const val TAB_CHAR = '\t'
        private const val CARRIAGE_RETURN_CHAR = '\r'
        private const val NEW_LINE_CHAR = '\n'

        private const val COLON_CHAR = ':'
        private const val COMMA_CHAR = ','

        private const val QUOTE_CHAR = '"'

        private const val LEFT_BRACE_CHAR = '{'
        private const val RIGHT_BRACE_CHAR = '}'
        private const val LEFT_BRACKET_CHAR = '['
        private const val RIGHT_BRACKET_CHAR = ']'
        private const val ESCAPE_CHAR = '\\'
    }
}
