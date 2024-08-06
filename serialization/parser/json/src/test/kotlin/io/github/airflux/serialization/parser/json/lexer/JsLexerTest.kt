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
import io.github.airflux.serialization.parser.json.escape
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly

internal class JsLexerTest : FreeSpec() {

    init {

        "The JsLexer type" - {

            "Skip chars" - {
                withData(
                    nameFn = { (input, td) -> nameFn(input, td) },
                    skipChars()
                ) { (input, expected) ->
                    val lexer = JsLexer(JsInputString(input))
                    val actual = lexer.scan()
                    actual shouldContainExactly expected
                }
            }

            "Terminal chars" - {
                withData(
                    nameFn = { (input, td) -> nameFn(input, td) },
                    terminalChars()
                ) { (input, expected) ->
                    val lexer = JsLexer(JsInputString(input))
                    val actual = lexer.scan()
                    actual shouldContainExactly expected
                }
            }

            "Separator chars" - {
                withData(
                    nameFn = { (input, td) -> nameFn(input, td) },
                    commaSeparatorChar() + colonSeparatorChar()
                ) { (input, expected) ->
                    val lexer = JsLexer(JsInputString(input))
                    val actual = lexer.scan()
                    actual shouldContainExactly expected
                }
            }

            "Literal Lexeme" - {
                withData(
                    nameFn = { (input, td) -> nameFn(input, td) },
                    literalLexeme()
                ) { (input, expected) ->
                    val lexer = JsLexer(JsInputString(input))
                    val actual = lexer.scan()
                    actual shouldContainExactly expected
                }
            }

            "Digital lexeme" - {
                withData(
                    nameFn = { (input, td) -> nameFn(input, td) },
                    digitalLexeme()
                ) { (input, expected) ->
                    val lexer = JsLexer(JsInputString(input))
                    val actual = lexer.scan()
                    actual shouldContainExactly expected
                }
            }

            "Escaping chars" - {
                withData(
                    nameFn = { (input, td) -> nameFn(input, td) },
                    escapingChars()
                ) { (input, expected) ->
                    val lexer = JsLexer(JsInputString(input))
                    val actual = lexer.scan()
                    actual shouldContainExactly expected
                }
            }

            "Curly brace chars" - {
                withData(
                    nameFn = { (input, td) -> nameFn(input, td) },
                    curlyBraceChars()
                ) { (input, expected) ->
                    val lexer = JsLexer(JsInputString(input))
                    val actual = lexer.scan()
                    actual shouldContainExactly expected
                }
            }

            "Square bracket chars" - {
                withData(
                    nameFn = { (input, td) -> nameFn(input, td) },
                    squareBracketChars()
                ) { (input, expected) ->
                    val lexer = JsLexer(JsInputString(input))
                    val actual = lexer.scan()
                    actual shouldContainExactly expected
                }
            }
        }
    }

    private fun JsLexer.scan(): List<TestData> {
        tailrec fun JsLexer.scan(list: List<TestData>): List<TestData> =
            when (val terminal = nextToken()) {
                Terminal.EOF -> list
                Terminal.ERROR -> list + TestData.Failure(error = this.error!!)
                else -> scan(
                    list + TestData.Success(
                        terminal = terminal,
                        lexeme = lexeme.buildString(),
                        position = lexeme.position,
                        line = lexeme.line,
                        column = lexeme.column
                    )
                )
            }

        return scan(emptyList())
    }

    private fun skipChars() = listOf(
        case("  abc", literal("abc", position = 2, line = 1, 3)),
        case('\t' + "abc", literal("abc", position = 1, line = 1, column = 2)),
        case('\r' + "abc", literal("abc", position = 1, line = 1, column = 2)),
        case('\n' + "abc", literal("abc", position = 1, line = 2, column = 2)),
    )

    private fun terminalChars() = listOf(
        case(
            "abc" + " " + "def",
            literal("abc", position = 0, line = 1, column = 1),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            "abc" + '\t' + "def",
            literal("abc", position = 0, line = 1, column = 1),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            "abc" + '\r' + "def",
            literal("abc", position = 0, line = 1, column = 1),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            "abc" + '\n' + "def",
            literal("abc", position = 0, line = 1, column = 1),
            literal("def", position = 4, line = 2, column = 2)
        ),
        case(
            "abc" + '{' + "def",
            literal("abc", position = 0, line = 1, column = 1),
            leftBrace(position = 3, line = 1, column = 4),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            "abc" + '}' + "def",
            literal("abc", position = 0, line = 1, column = 1),
            rightBrace(position = 3, line = 1, column = 4),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            "abc" + '[' + "def",
            literal("abc", position = 0, line = 1, column = 1),
            leftBracket(position = 3, line = 1, column = 4),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            "abc" + ']' + "def",
            literal("abc", position = 0, line = 1, column = 1),
            rightBracket(position = 3, line = 1, column = 4),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            "abc" + ',' + "def",
            literal("abc", position = 0, line = 1, column = 1),
            comma(position = 3, line = 1, column = 4),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            "abc" + ':' + "def",
            literal("abc", position = 0, line = 1, column = 1),
            colon(position = 3, line = 1, column = 4),
            literal("def", position = 4, line = 1, column = 5)
        )
    )

    private fun commaSeparatorChar() = listOf(
        case(
            "abc,def",
            literal("abc", position = 0, line = 1, column = 1),
            comma(position = 3, line = 1, column = 4),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            ",abc,",
            comma(position = 0, line = 1, column = 1),
            literal("abc", position = 1, line = 1, column = 2),
            comma(position = 4, line = 1, column = 5)
        )
    )

    private fun colonSeparatorChar() = listOf(
        case(
            "1:2",
            literal("1", position = 0, line = 1, column = 1),
            colon(position = 1, line = 1, column = 2),
            literal("2", position = 2, line = 1, column = 3)
        ),
        case(
            "1:abc",
            literal("1", position = 0, line = 1, column = 1),
            colon(position = 1, line = 1, column = 2),
            literal("abc", position = 2, line = 1, column = 3)
        ),
        case(
            "abc:1",
            literal("abc", position = 0, line = 1, column = 1),
            colon(position = 3, line = 1, column = 4),
            literal("1", position = 4, line = 1, column = 5)
        ),

        case(
            "abc:def",
            literal("abc", position = 0, line = 1, column = 1),
            colon(position = 3, line = 1, column = 4),
            literal("def", position = 4, line = 1, column = 5)
        ),
        case(
            ":abc:",
            colon(position = 0, line = 1, column = 1),
            literal("abc", position = 1, line = 1, column = 2),
            colon(position = 4, line = 1, column = 5)
        ),
        case(
            ":1:",
            colon(position = 0, line = 1, column = 1),
            literal("1", position = 1, line = 1, column = 2),
            colon(position = 2, line = 1, column = 3)
        )
    )

    private fun curlyBraceChars() = listOf(
        case(
            "{",
            leftBrace(position = 0, line = 1, column = 1),
        ),
        case(
            "}",
            rightBrace(position = 0, line = 1, column = 1),
        ),
        case(
            "{{",
            leftBrace(position = 0, line = 1, column = 1),
            leftBrace(position = 1, line = 1, column = 2),
        ),
        case(
            "}}",
            rightBrace(position = 0, line = 1, column = 1),
            rightBrace(position = 1, line = 1, column = 2),
        ),
        case(
            "{}",
            leftBrace(position = 0, line = 1, column = 1),
            rightBrace(position = 1, line = 1, column = 2),
        ),
        case(
            "{abc}",
            leftBrace(position = 0, line = 1, column = 1),
            literal("abc", position = 1, line = 1, column = 2),
            rightBrace(position = 4, line = 1, column = 5)
        ),
        case(
            "{\"abc\"}",
            leftBrace(position = 0, line = 1, column = 1),
            literal("\"abc\"", position = 1, line = 1, column = 2),
            rightBrace(position = 6, line = 1, column = 7)
        )
    )

    private fun squareBracketChars() = listOf(
        case(
            "[",
            leftBracket(position = 0, line = 1, column = 1),
        ),
        case(
            "]",
            rightBracket(position = 0, line = 1, column = 1),
        ),
        case(
            "[[",
            leftBracket(position = 0, line = 1, column = 1),
            leftBracket(position = 1, line = 1, column = 2),
        ),
        case(
            "]]",
            rightBracket(position = 0, line = 1, column = 1),
            rightBracket(position = 1, line = 1, column = 2),
        ),
        case(
            "[]",
            leftBracket(position = 0, line = 1, column = 1),
            rightBracket(position = 1, line = 1, column = 2),
        ),
        case(
            "[abc]",
            leftBracket(position = 0, line = 1, column = 1),
            literal("abc", position = 1, line = 1, column = 2),
            rightBracket(position = 4, line = 1, column = 5)
        ),
        case(
            "[\"abc\"]",
            leftBracket(position = 0, line = 1, column = 1),
            literal("\"abc\"", position = 1, line = 1, column = 2),
            rightBracket(position = 6, line = 1, column = 7)
        ),
    )

    private fun digitalLexeme() = listOf(
        case("0", literal("0", position = 0, line = 1, 1)),
        case("-0", literal("-0", position = 0, line = 1, 1)),
        case("1234567890", literal("1234567890", position = 0, line = 1, 1)),
        case("0.0", literal("0.0", position = 0, line = 1, 1)),
        case("-1.1234567890", literal("-1.1234567890", position = 0, line = 1, 1)),
        case("1e-23", literal("1e-23", position = 0, line = 1, 1)),
        case("+1e-23", literal("+1e-23", position = 0, line = 1, 1)),
        case("-1e-23", literal("-1e-23", position = 0, line = 1, 1)),
        case("-1.2e3", literal("-1.2e3", position = 0, line = 1, 1)),
        case("1e0001", literal("1e0001", position = 0, line = 1, 1)),
    )

    @Suppress("LongMethod")
    private fun literalLexeme() = listOf(
        case("abc", literal("abc", position = 0, line = 1, 1)),
        case("abc+1e-23", literal("abc+1e-23", position = 0, line = 1, 1)),

        /* Quotes */
        case("\"abc\"", literal("\"abc\"", position = 0, line = 1, 1)),
        case(
            "\"abc\"def",
            literal("\"abc\"", position = 0, line = 1, column = 1),
            literal("def", position = 5, line = 1, column = 6)
        ),
        case(
            "abc\"def\"",
            literal("abc", position = 0, line = 1, column = 1),
            literal("\"def\"", position = 3, line = 1, column = 4)
        ),

        case("\"abc def\"", literal("\"abc def\"", position = 0, line = 1, column = 1)),
        case(
            "\"abc\" def",
            literal("\"abc\"", position = 0, line = 1, column = 1),
            literal("def", position = 6, line = 1, column = 7)
        ),
        case(
            "abc \"def\"",
            literal("abc", position = 0, line = 1, column = 1),
            literal("\"def\"", position = 4, line = 1, column = 5)
        ),
        case("\"abc,def\"", literal("\"abc,def\"", position = 0, line = 1, column = 1)),
        case(
            "\"abc\",def",
            literal("\"abc\"", position = 0, line = 1, column = 1),
            comma(position = 5, line = 1, column = 6),
            literal("def", position = 6, line = 1, column = 7)
        ),
        case(
            "abc,\"def\"",
            literal("abc", position = 0, line = 1, column = 1),
            comma(position = 3, line = 1, column = 4),
            literal("\"def\"", position = 4, line = 1, column = 5)
        ),
        case("\"abc:def\"", literal("\"abc:def\"", position = 0, line = 1, column = 1)),
        case(
            "\"abc\":def",
            literal("\"abc\"", position = 0, line = 1, column = 1),
            colon(position = 5, line = 1, column = 6),
            literal("def", position = 6, line = 1, column = 7)
        ),
        case(
            "abc:\"def\"",
            literal("abc", position = 0, line = 1, column = 1),
            colon(position = 3, line = 1, column = 4),
            literal("\"def\"", position = 4, line = 1, column = 5)
        ),
        case("\"{abc}\"", literal("\"{abc}\"", position = 0, line = 1, column = 1)),
        case("\"[abc]\"", literal("\"[abc]\"", position = 0, line = 1, column = 1)),
        case(
            "\"a,b:c,{}[] ",
            literal(lexeme = "\"a,b:c,{}[] ", position = 0, line = 1, column = 1)
        ),
        case(
            "abc\"",
            literal("abc", position = 0, line = 1, column = 1),
            literal(lexeme = "\"", position = 3, line = 1, column = 4)
        )

    )

    private fun escapingChars() = listOf(
        // "
        case("\"a\\\"c\"", literal("\"a\"c\"", position = 0, line = 1, column = 1)),

        // \\
        case("\"a\\\\c\"", literal("\"a\\c\"", position = 0, line = 1, column = 1)),

        // /
        case("\"a\\/c\"", literal("\"a/c\"", position = 0, line = 1, column = 1)),

        // \b
        case("\"a\\bc\"", literal("\"a\bc\"", position = 0, line = 1, column = 1)),

        // \f
        case("\"a\\fc\"", literal("\"a\\fc\"", position = 0, line = 1, column = 1)),

        // \n
        case("\"a\\nc\"", literal("\"a\nc\"", position = 0, line = 1, column = 1)),

        // \r
        case("\"a\\rc\"", literal("\"a\rc\"", position = 0, line = 1, column = 1)),

        //t
        case("\"a\\tc\"", literal("\"a\tc\"", position = 0, line = 1, column = 1)),

        // \u
        case("\"a\\u00A9c\"", literal("\"a\\u00A9c\"", position = 0, line = 1, column = 1)),

        case("\"a\\cd\"", unrecognizedCharacterEscape('c', position = 3, line = 1, column = 4)),
        case("a\\tb", unexpectedCharacter('\\', position = 1, line = 1, column = 2))
    )

    private fun case(input: String, vararg tokens: TestData) = Pair(input, tokens.toList())

    private fun literal(lexeme: String, position: Int, line: Int, column: Int) =
        TestData.Success(Terminal.LITERAL, lexeme, position, line, column)

    private fun comma(position: Int, line: Int, column: Int) =
        TestData.Success(Terminal.SEPARATOR_COMMA, ",", position, line, column)

    private fun colon(position: Int, line: Int, column: Int) =
        TestData.Success(Terminal.SEPARATOR_COLON, lexeme = ":", position, line, column)

    private fun leftBrace(position: Int, line: Int, column: Int) =
        TestData.Success(Terminal.BRACE_LEFT, "{", position, line, column)

    private fun rightBrace(position: Int, line: Int, column: Int) =
        TestData.Success(Terminal.BRACE_RIGHT, "}", position, line, column)

    private fun leftBracket(position: Int, line: Int, column: Int) =
        TestData.Success(Terminal.BRACKET_LEFT, "[", position, line, column)

    private fun rightBracket(position: Int, line: Int, column: Int) =
        TestData.Success(Terminal.BRACKET_RIGHT, "]", position, line, column)

    private fun unrecognizedCharacterEscape(char: Char, position: Int, line: Int, column: Int) =
        failure(cause = JsParserErrors.UnrecognizedCharacterEscape(char, position, line, column))

    private fun unexpectedCharacter(char: Char, position: Int, line: Int, column: Int) =
        failure(cause = JsParserErrors.UnexpectedCharacter(char, position, line, column))

    private fun failure(cause: JsParserErrors) = TestData.Failure(error = cause)

    sealed class TestData {
        abstract fun description(): String

        data class Success(
            val terminal: Terminal,
            val lexeme: String,
            val position: Int,
            val line: Int,
            val column: Int
        ) : TestData() {
            override fun description(): String = lexeme.replace(WHITESPACE, WHITESPACE_GRAPH)
        }

        data class Failure(
            val error: JsParserErrors,
        ) : TestData() {
            override fun description(): String = "error: $error"
        }
    }

    companion object {
        private const val WHITESPACE = ' '
        private const val WHITESPACE_GRAPH = '\u02FD'

        private fun nameFn(input: String, results: List<TestData>) =
            input.escape() +
                " -> lexemes (${results.count { it is TestData.Success }}): " +
                results.joinToString(separator = " | ") { it.description() }
    }
}
