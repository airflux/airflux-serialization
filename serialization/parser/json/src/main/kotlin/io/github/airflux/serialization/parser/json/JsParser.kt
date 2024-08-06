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

import io.github.airflux.serialization.parser.json.lexer.JsLexeme
import io.github.airflux.serialization.parser.json.lexer.JsLexer
import io.github.airflux.serialization.parser.json.lexer.JsSourceInput
import io.github.airflux.serialization.parser.json.lexer.Terminal
import io.github.airflux.serialization.parser.json.lexer.isFalseToken
import io.github.airflux.serialization.parser.json.lexer.isNullToken
import io.github.airflux.serialization.parser.json.lexer.isNumberToken
import io.github.airflux.serialization.parser.json.lexer.isTrueToken

@Suppress("TooManyFunctions")
internal class JsParser(
    input: JsSourceInput,
    private val propertyNamePool: PropertyNamePool? = null
) {
    private val lexer: JsLexer = JsLexer(input)
    private val parsingContext = Stack<State>()
    private val valueBuilder = JsValueBuilder()
    private var error: JsParserErrors? = null

    fun parse(): JsParserResult {
        parsingContext.push(State.INITIAL)
        var status: ProcessingStatus = ProcessingStatus.CONTINUE
        while (status == ProcessingStatus.CONTINUE) {
            val state = parsingContext.peek()
            status = when (state) {
                State.INITIAL -> initial()
                State.START_OBJECT -> startObject()
                State.PROPERTY_NAME -> propertyName()
                State.COLON -> colon()
                State.PROPERTY_VALUE -> propertyValue()
                State.PROPERTY_SEPARATOR -> propertiesSeparator()
                State.END_OBJECT -> endObject()
                State.START_ARRAY -> startArray()
                State.ARRAY_ITEM -> arrayItem()
                State.ARRAY_ITEM_SEPARATOR -> arrayItemSeparator()
                State.END_ARRAY -> endArray()
            }
        }

        return if (status == ProcessingStatus.ERROR)
            JsParserResult.Failure(error!!)
        else
            JsParserResult.Success(valueBuilder.result)
    }

    private fun initial(): ProcessingStatus =
        when (nextToken()) {
            Terminal.EOF -> ProcessingStatus.END
            Terminal.BRACE_LEFT -> transitionToStartObject()
            Terminal.BRACKET_LEFT -> transitionToStartArray()
            Terminal.LITERAL -> valueLiteral()

            Terminal.BRACE_RIGHT,
            Terminal.BRACKET_RIGHT,
            Terminal.SEPARATOR_COLON,
            Terminal.SEPARATOR_COMMA -> transitionToUnexpectedCharacterError(reason = EXPECTED_VALID_VALUE)

            Terminal.ERROR -> transitionToError(error!!)
        }

    private fun startObject(): ProcessingStatus =
        when (nextToken()) {
            Terminal.EOF -> transitionToUnexpectedEndOfInputError()
            Terminal.LITERAL -> propertyNameLiteral()
            Terminal.BRACE_RIGHT -> transitionToEndObject()

            Terminal.BRACE_LEFT,
            Terminal.BRACKET_LEFT,
            Terminal.BRACKET_RIGHT,
            Terminal.SEPARATOR_COLON,
            Terminal.SEPARATOR_COMMA -> transitionToUnexpectedCharacterError(reason = EXPECTED_PROPERTY_NAME)

            Terminal.ERROR -> transitionToError(error!!)
        }

    private fun propertyName(): ProcessingStatus {
        parsingContext.pop() // PROPERTY_NAME
        return when (nextToken()) {
            Terminal.EOF -> transitionToUnexpectedEndOfInputError(EXPECTED_COLON)
            Terminal.SEPARATOR_COLON -> colonSeparator()

            Terminal.LITERAL,
            Terminal.BRACE_LEFT,
            Terminal.BRACE_RIGHT,
            Terminal.BRACKET_LEFT,
            Terminal.BRACKET_RIGHT,
            Terminal.SEPARATOR_COMMA -> transitionToUnrecognizedTokenError(reason = EXPECTED_COLON)

            Terminal.ERROR -> transitionToError(error!!)
        }
    }

    private fun colon(): ProcessingStatus {
        parsingContext.pop() // COLON
        return when (nextToken()) {
            Terminal.EOF -> transitionToUnexpectedEndOfInputError()
            Terminal.LITERAL -> propertyValueLiteral()
            Terminal.BRACE_LEFT -> transitionToStartObject()
            Terminal.BRACKET_LEFT -> transitionToStartArray()

            Terminal.BRACE_RIGHT,
            Terminal.BRACKET_RIGHT,
            Terminal.SEPARATOR_COLON,
            Terminal.SEPARATOR_COMMA -> transitionToUnexpectedCharacterError(reason = EXPECTED_VALID_VALUE)

            Terminal.ERROR -> transitionToError(error!!)
        }
    }

    private fun propertyValue(): ProcessingStatus {
        parsingContext.pop() // PROPERTY_VALUE
        return when (nextToken()) {
            Terminal.EOF -> transitionToUnexpectedEndOfInputError()
            Terminal.SEPARATOR_COMMA -> {
                parsingContext.push(State.PROPERTY_SEPARATOR)
                ProcessingStatus.CONTINUE
            }

            Terminal.BRACE_RIGHT -> transitionToEndObject()
            Terminal.LITERAL -> transitionToUnrecognizedTokenError(reason = EXPECTED_SEPARATOR_OR_RBRACE)

            Terminal.BRACE_LEFT,
            Terminal.BRACKET_LEFT,
            Terminal.BRACKET_RIGHT,
            Terminal.SEPARATOR_COLON -> transitionToUnexpectedCharacterError(reason = EXPECTED_SEPARATOR_OR_RBRACE)

            Terminal.ERROR -> transitionToError(error!!)
        }
    }

    private fun propertiesSeparator(): ProcessingStatus {
        parsingContext.pop() // PROPERTY_SEPARATOR
        return when (nextToken()) {
            Terminal.EOF -> transitionToUnexpectedEndOfInputError()
            Terminal.LITERAL -> propertyNameLiteral()

            Terminal.BRACE_LEFT,
            Terminal.BRACE_RIGHT,
            Terminal.BRACKET_LEFT,
            Terminal.BRACKET_RIGHT,
            Terminal.SEPARATOR_COLON,
            Terminal.SEPARATOR_COMMA -> transitionToUnexpectedCharacterError(reason = EXPECTED_PROPERTY_NAME)

            Terminal.ERROR -> transitionToError(error!!)
        }
    }

    private fun endObject(): ProcessingStatus {
        parsingContext.pop() // END_OBJECT
        return endOfContainer()
    }

    private fun startArray(): ProcessingStatus {
        return when (nextToken()) {
            Terminal.EOF -> transitionToUnexpectedEndOfInputError()
            Terminal.BRACE_LEFT -> transitionToStartObject()
            Terminal.BRACKET_LEFT -> transitionToStartArray()
            Terminal.LITERAL -> arrayItemLiteral()
            Terminal.BRACKET_RIGHT -> transitionToEndArray()

            Terminal.BRACE_RIGHT,
            Terminal.SEPARATOR_COLON,
            Terminal.SEPARATOR_COMMA -> transitionToUnexpectedCharacterError(EXPECTED_VALID_VALUE)

            Terminal.ERROR -> transitionToError(error!!)
        }
    }

    private fun arrayItem(): ProcessingStatus {
        parsingContext.pop() // ARRAY_ITEM
        return when (nextToken()) {
            Terminal.EOF -> transitionToUnexpectedEndOfInputError(EXPECTED_SEPARATOR_OR_RBRACKET)
            Terminal.SEPARATOR_COMMA -> {
                parsingContext.push(State.ARRAY_ITEM_SEPARATOR)
                ProcessingStatus.CONTINUE
            }

            Terminal.BRACKET_RIGHT -> transitionToEndArray()
            Terminal.LITERAL -> transitionToUnrecognizedTokenError(EXPECTED_SEPARATOR_OR_RBRACKET)

            Terminal.BRACE_LEFT,
            Terminal.BRACE_RIGHT,
            Terminal.BRACKET_LEFT,
            Terminal.SEPARATOR_COLON -> transitionToUnexpectedCharacterError(reason = EXPECTED_SEPARATOR_OR_RBRACKET)

            Terminal.ERROR -> transitionToError(error!!)
        }
    }

    private fun arrayItemSeparator(): ProcessingStatus {
        parsingContext.pop() // ARRAY_ITEM_SEPARATOR
        return when (nextToken()) {
            Terminal.EOF -> transitionToUnexpectedEndOfInputError(reason = " within/between Array entries")
            Terminal.BRACE_LEFT -> transitionToStartObject()
            Terminal.BRACKET_LEFT -> transitionToStartArray()
            Terminal.LITERAL -> arrayItemLiteral()

            Terminal.BRACE_RIGHT,
            Terminal.BRACKET_RIGHT,
            Terminal.SEPARATOR_COLON,
            Terminal.SEPARATOR_COMMA -> transitionToUnexpectedCharacterError(EXPECTED_VALID_VALUE)

            Terminal.ERROR -> transitionToError(error!!)
        }
    }

    private fun endArray(): ProcessingStatus {
        parsingContext.pop() // END_ARRAY
        return endOfContainer()
    }

    private fun endOfContainer(): ProcessingStatus = when (nextToken()) {
        Terminal.EOF -> ProcessingStatus.CONTINUE

        Terminal.SEPARATOR_COMMA ->
            if (isObjectScope()) {
                parsingContext.push(State.PROPERTY_SEPARATOR)
                ProcessingStatus.CONTINUE
            } else if (isArrayScope()) {
                parsingContext.push(State.ARRAY_ITEM_SEPARATOR)
                ProcessingStatus.CONTINUE
            } else
                transitionToUnexpectedCharacterError()

        Terminal.BRACE_RIGHT ->
            if (isObjectScope())
                transitionToEndObject()
            else
                transitionToUnexpectedCharacterError()

        Terminal.BRACKET_RIGHT ->
            if (isArrayScope())
                transitionToEndArray()
            else
                transitionToUnexpectedCharacterError()

        Terminal.LITERAL -> transitionToUnrecognizedTokenError()

        Terminal.BRACE_LEFT,
        Terminal.BRACKET_LEFT,
        Terminal.SEPARATOR_COLON -> transitionToUnexpectedCharacterError()

        Terminal.ERROR -> transitionToError(error!!)
    }

    private fun nextToken(): Terminal {
        val token = lexer.nextToken()
        if (token == Terminal.ERROR) {
            error = lexer.error
        }
        return token
    }

    private fun isObjectScope() = parsingContext.peek() == State.START_OBJECT

    private fun isArrayScope() = parsingContext.peek() == State.START_ARRAY

    private fun propertyNameLiteral() =
        if (lexer.lexeme.startsWith(QUOTE_CHAR)) {
            if (lexer.lexeme.endsWith(QUOTE_CHAR))
                transitionToPropertyName(StringLexeme(lexer.lexeme))
            else
                transitionToUnexpectedEndOfInputError(reason = END_OF_PROPERTY_NAME)
        } else
            transitionToUnexpectedCharacterError(reason = EXPECTED_PROPERTY_NAME)

    private fun colonSeparator(): ProcessingStatus {
        parsingContext.push(State.COLON)
        return ProcessingStatus.CONTINUE
    }

    private fun propertyValueLiteral() = valueLiteral(State.PROPERTY_VALUE)

    private fun arrayItemLiteral() = valueLiteral(State.ARRAY_ITEM)

    private fun valueLiteral(state: State? = null): ProcessingStatus {
        val status = if (lexer.lexeme.startsWith(QUOTE_CHAR)) {
            if (lexer.lexeme.endsWith(QUOTE_CHAR))
                transitionToValue(StringLexeme(lexer.lexeme))
            else
                transitionToUnexpectedEndOfInputError(reason = EXPECTED_CLOSING_QUOTE)
        } else if (lexer.lexeme.isNumberToken())
            transitionToValue(NumberLexeme(lexer.lexeme))
        else if (lexer.lexeme.isTrueToken())
            transitionToValue(true)
        else if (lexer.lexeme.isFalseToken())
            transitionToValue(false)
        else if (lexer.lexeme.isNullToken())
            transitionToNullValue()
        else
            transitionToUnrecognizedTokenError(reason = EXPECTED_VALID_VALUE)

        if (status == ProcessingStatus.CONTINUE && state != null) parsingContext.push(state)
        return status
    }

    private fun transitionToStartObject(): ProcessingStatus {
        parsingContext.push(State.START_OBJECT)
        valueBuilder.startObject()
        return ProcessingStatus.CONTINUE
    }

    private fun transitionToEndObject(): ProcessingStatus {
        parsingContext.pop() // START_OBJECT
        parsingContext.push(State.END_OBJECT)
        valueBuilder.endObject()
        return ProcessingStatus.CONTINUE
    }

    private fun transitionToStartArray(): ProcessingStatus {
        parsingContext.push(State.START_ARRAY)
        valueBuilder.startArray()
        return ProcessingStatus.CONTINUE
    }

    private fun transitionToEndArray(): ProcessingStatus {
        parsingContext.pop() // START_ARRAY
        parsingContext.push(State.END_ARRAY)
        valueBuilder.endArray()
        return ProcessingStatus.CONTINUE
    }

    private fun transitionToPropertyName(value: StringLexeme): ProcessingStatus {
        parsingContext.push(State.PROPERTY_NAME)
        valueBuilder.addProperty(value.buildPropertyName())
        return ProcessingStatus.CONTINUE
    }

    private fun transitionToValue(value: NumberLexeme): ProcessingStatus {
        valueBuilder.numberValue(value.buildString())
        return ProcessingStatus.CONTINUE
    }

    private fun transitionToValue(value: StringLexeme): ProcessingStatus {
        valueBuilder.stringValue(value.buildString())
        return ProcessingStatus.CONTINUE
    }

    private fun transitionToValue(value: Boolean): ProcessingStatus {
        valueBuilder.booleanValue(value)
        return ProcessingStatus.CONTINUE
    }

    private fun transitionToNullValue(): ProcessingStatus {
        valueBuilder.nullValue()
        return ProcessingStatus.CONTINUE
    }

    private fun transitionToUnexpectedEndOfInputError(reason: String? = null): ProcessingStatus =
        transitionToError(
            JsParserErrors.UnexpectedEndOfInput(
                position = lexer.position,
                line = lexer.line,
                column = lexer.column,
                reason = reason
            )
        )

    private fun transitionToUnrecognizedTokenError(reason: String? = null): ProcessingStatus =
        transitionToError(
            JsParserErrors.UnrecognizedToken(
                token = lexer.lexeme.buildString(),
                position = lexer.lexeme.position,
                line = lexer.lexeme.line,
                column = lexer.lexeme.column,
                reason = reason
            )
        )

    private fun transitionToUnexpectedCharacterError(reason: String? = null): ProcessingStatus =
        transitionToError(
            JsParserErrors.UnexpectedCharacter(
                char = lexer.lexeme.first(),
                position = lexer.lexeme.position,
                line = lexer.lexeme.line,
                column = lexer.lexeme.column,
                reason = reason
            )
        )

    private fun transitionToError(error: JsParserErrors): ProcessingStatus {
        this.error = error
        return ProcessingStatus.ERROR
    }

    private fun StringLexeme.buildPropertyName(): String {
        val lexeme = this.get
        val start = 1
        val end = lexeme.length - 1
        return if (propertyNamePool != null)
            propertyNamePool.getOrPut(lexeme, start, end)
        else
            lexeme.buildString(start, end)
    }

    private fun StringLexeme.buildString(): String {
        val lexeme = this.get
        val start = 1
        val end = lexeme.length - 1
        return lexeme.buildString(start, end)
    }

    private fun NumberLexeme.buildString(): String = this.get.buildString()

    private companion object {
        private const val QUOTE_CHAR = '"'
        private const val VALID_VALUES = "(String, Number, Array, Object or token 'null', 'true' or 'false')"
        private const val EXPECTED_VALID_VALUE = ": expected a valid value $VALID_VALUES"

        private const val EXPECTED_CLOSING_QUOTE = ": expected closing quote for a string value"

        private const val EXPECTED_PROPERTY_NAME = ": expected double-quote to start property name"
        private const val END_OF_PROPERTY_NAME = " of property name entry"
        private const val EXPECTED_COLON = ": expected a colon to separate property name and value"

        private const val EXPECTED_SEPARATOR_OR_RBRACE =
            ": expected comma to separate Object properties or close marker '}'"

        private const val EXPECTED_SEPARATOR_OR_RBRACKET =
            ": expected comma to separate Array items or close marker ']'"
    }

    private enum class State {
        INITIAL,

        START_OBJECT,
        PROPERTY_NAME,
        COLON,
        PROPERTY_VALUE,
        PROPERTY_SEPARATOR,
        END_OBJECT,

        START_ARRAY,
        ARRAY_ITEM,
        ARRAY_ITEM_SEPARATOR,
        END_ARRAY
    }

    private enum class ProcessingStatus {
        CONTINUE,
        END,
        ERROR
    }

    @JvmInline
    private value class NumberLexeme(val get: JsLexeme)

    @JvmInline
    private value class StringLexeme(val get: JsLexeme)
}
