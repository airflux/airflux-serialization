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

import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.parser.json.lexer.JsInputString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@Suppress("LargeClass")
internal class JsParserTest : FreeSpec() {

    init {

        "The JsParser type" - {

            "when the input is valid JSON" - {

                withData(
                    nameFn = { (input, expected) ->
                        "then the parser for input `${nameFn(input)}` should return the expected `$expected`"
                    },
                    validJsons()
                ) { (json, expected) ->
                    val parser = JsParser(JsInputString(json), PropertyNamePool())
                    val actual = parser.parse()

                    actual.shouldBeInstanceOf<JsParserResult.Success>()

                    expected shouldBe actual.value
                }
            }

            "when the input is invalid JSON" - {

                withData(
                    nameFn = { (input, events) -> nameFn(input, events) },
                    invalidJsons()
                ) { (json, expected) ->
                    val parser = JsParser(JsInputString(json), PropertyNamePool())
                    val actual = parser.parse()

                    expected shouldBe actual
                }
            }
        }
    }

    private fun validJsons(): List<Pair<String, JsValue?>> =
        validJsonContainsSimpleValue() +
            validJsonContainsArray() +
            validJsonContainsObject()

    private fun validJsonContainsSimpleValue(): List<Pair<String, JsValue?>> = listOf(
        "" to null,
        " \"name\" " to stringValue("name"),
        " true " to booleanValue(true),
        " false " to booleanValue(false),
        " 123 " to numberValue("123"),
        " null " to nullValue()
    )

    @Suppress("LongMethod")
    private fun validJsonContainsObject(): List<Pair<String, JsValue>> = listOf(
        "{}" to struct(),
        """ {"a": "b"} """ to struct("a" to stringValue("b")),
        """ {"a": 123} """ to struct("a" to numberValue("123")),
        """ {"a": true} """ to struct("a" to booleanValue(true)),
        """ {"a": false} """ to struct("a" to booleanValue(false)),
        """ {"a": null} """ to struct("a" to nullValue()),
        """ {"a": []} """ to struct("a" to array()),
        """ {"a": [true, false]} """ to
            struct("a" to array(booleanValue(true), booleanValue(false))),
        """ {"a": {}} """ to struct("a" to struct()),
        """ {"a": {"b": 123}} """ to
            struct("a" to struct("b" to numberValue("123"))),
        """ {"a": 123, "b": 456} """
            to struct("a" to numberValue("123"), "b" to numberValue("456")),
        """ {"a": {}, "b": {}} """ to struct("a" to struct(), "b" to struct()),
        """ {"a": [], "b": []} """ to struct("a" to array(), "b" to array()),
    )

    @Suppress("LongMethod")
    private fun validJsonContainsArray(): List<Pair<String, JsValue>> = listOf(
        "[]" to array(),
        "[[]]" to array(array()),
        "[[], []]" to array(array(), array()),
        "[{}]" to array(struct()),
        "[{},{}]" to array(struct(), struct()),
        " [\"a\"] " to array(stringValue("a")),
        " [123] " to array(numberValue("123")),
        " [true] " to array(booleanValue(true)),
        " [false] " to array(booleanValue(false)),
        " [null] " to array(nullValue()),
        """ ["a", true, false, 123, null] """ to array(
            stringValue("a"),
            booleanValue(true),
            booleanValue(false),
            numberValue("123"),
            nullValue()
        ),
        """ [["a"]] """ to array(array(stringValue("a"))),
        """ [["a", null]] """ to array(array(stringValue("a"), nullValue())),
        """ [["a"], null] """ to array(array(stringValue("a")), nullValue()),
        """ [["a"], [null]] """ to array(array(stringValue("a")), array(nullValue())),
        """ [["a", 123], [true, false]] """ to array(
            array(stringValue("a"), numberValue("123")),
            array(booleanValue(true), booleanValue(false))
        ),
        " [{}, {}] " to array(struct(), struct()),
        """ [{"a": "123"}, {"b": 456}] """ to array(
            struct("a" to stringValue("123")),
            struct("b" to numberValue("456"))
        ),
    )

    @Suppress("LongMethod")
    private fun invalidJsons(): List<Pair<String, JsParserResult.Failure>> = listOf(
        "\\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 0,
                line = 1,
                column = 1
            )
        ),
        "]" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ']',
                position = 0,
                line = 1,
                column = 1,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "}" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '}',
                position = 0,
                line = 1,
                column = 1,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        ":" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 0,
                line = 1,
                column = 1,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "," to error(
            JsParserErrors.UnexpectedCharacter(
                char = ',',
                position = 0,
                line = 1,
                column = 1,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )

        ),
        "tru" to error(
            JsParserErrors.UnrecognizedToken(
                token = "tru",
                position = 0,
                line = 1,
                column = 1,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "\"123 " to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected closing quote for a string value"
            ),
        )
    ) + invalidJsonContainsArray() + invalidJsonContainsObject()

    @Suppress("LongMethod")
    private fun invalidJsonContainsArray(): List<Pair<String, JsParserResult.Failure>> = listOf(
        "[" to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 1,
                line = 1,
                column = 2
            )
        ),
        "[\\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 1,
                line = 1,
                column = 2
            )
        ),
        "[{" to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "[}" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '}',
                position = 1,
                line = 1,
                column = 2,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "[[" to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "[:" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 1,
                line = 1,
                column = 2,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "[," to error(
            JsParserErrors.UnexpectedCharacter(
                char = ',',
                position = 1,
                line = 1,
                column = 2,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "[true" to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected comma to separate Array items or close marker ']'"
            )
        ),
        "[true \\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 6,
                line = 1,
                column = 7
            )
        ),
        "[true false" to error(
            JsParserErrors.UnrecognizedToken(
                token = "false",
                position = 6,
                line = 1,
                column = 7,
                reason = ": expected comma to separate Array items or close marker ']'"
            )
        ),
        "[true{" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '{',
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected comma to separate Array items or close marker ']'"
            )
        ),
        "[true}" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '}',
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected comma to separate Array items or close marker ']'"
            )
        ),
        "[true[" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '[',
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected comma to separate Array items or close marker ']'"
            )
        ),
        "[true:" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected comma to separate Array items or close marker ']'"
            )
        ),
        "[true," to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 6,
                line = 1,
                column = 7,
                reason = " within/between Array entries"
            )
        ),
        "[true,\\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 6,
                line = 1,
                column = 7
            )
        ),
        "[true,fals" to error(
            JsParserErrors.UnrecognizedToken(
                token = "fals",
                position = 6,
                line = 1,
                column = 7,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "[true,}" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '}',
                position = 6,
                line = 1,
                column = 7,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "[true,]" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ']',
                position = 6,
                line = 1,
                column = 7,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "[true,," to error(
            JsParserErrors.UnexpectedCharacter(
                char = ',',
                position = 6,
                line = 1,
                column = 7,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "[true,:" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 6,
                line = 1,
                column = 7,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "[]\\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "[]true " to error(
            JsParserErrors.UnrecognizedToken(
                token = "true",
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "[]{ " to error(
            JsParserErrors.UnexpectedCharacter(
                char = '{',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "[]} " to error(
            JsParserErrors.UnexpectedCharacter(
                char = '}',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "[]] " to error(
            JsParserErrors.UnexpectedCharacter(
                char = ']',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "[][ " to error(
            JsParserErrors.UnexpectedCharacter(
                char = '[',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "[]: " to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "[], " to error(
            JsParserErrors.UnexpectedCharacter(
                char = ',',
                position = 2,
                line = 1,
                column = 3
            )
        )
    )

    @Suppress("LongMethod")
    private fun invalidJsonContainsObject(): List<Pair<String, JsParserResult.Failure>> = listOf(
        "{" to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 1,
                line = 1,
                column = 2
            )

        ),
        "{\\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 1,
                line = 1,
                column = 2
            )
        ),
        "{{" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '{',
                position = 1,
                line = 1,
                column = 2,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{[" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '[',
                position = 1,
                line = 1,
                column = 2,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{]" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ']',
                position = 1,
                line = 1,
                column = 2,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{:" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 1,
                line = 1,
                column = 2,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{," to error(
            JsParserErrors.UnexpectedCharacter(
                char = ',',
                position = 1,
                line = 1,
                column = 2,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{tru" to error(
            JsParserErrors.UnexpectedCharacter(
                char = 't',
                position = 1,
                line = 1,
                column = 2,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{\"a " to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 4,
                line = 1,
                column = 5,
                reason = " of property name entry"
            )
        ),
        "{\"a\" " to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected a colon to separate property name and value"
            )
        ),
        "{\"a\"\\ " to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 4,
                line = 1,
                column = 5
            )
        ),
        "{\"a\"true " to error(
            JsParserErrors.UnrecognizedToken(
                token = "true",
                position = 4,
                line = 1,
                column = 5,
                reason = ": expected a colon to separate property name and value"
            )
        ),
        "{\"a\"{" to error(
            JsParserErrors.UnrecognizedToken(
                token = "{",
                position = 4,
                line = 1,
                column = 5,
                reason = ": expected a colon to separate property name and value"
            )
        ),
        "{\"a\"}" to error(
            JsParserErrors.UnrecognizedToken(
                token = "}",
                position = 4,
                line = 1,
                column = 5,
                reason = ": expected a colon to separate property name and value"
            )
        ),
        "{\"a\"[" to error(
            JsParserErrors.UnrecognizedToken(
                token = "[",
                position = 4,
                line = 1,
                column = 5,
                reason = ": expected a colon to separate property name and value"
            )
        ),
        "{\"a\"]" to error(
            JsParserErrors.UnrecognizedToken(
                token = "]",
                position = 4,
                line = 1,
                column = 5,
                reason = ": expected a colon to separate property name and value"
            )
        ),
        "{\"a\", " to error(
            JsParserErrors.UnrecognizedToken(
                token = ",",
                position = 4,
                line = 1,
                column = 5,
                reason = ": expected a colon to separate property name and value"
            )
        ),
        "{\"a\":" to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 5,
                line = 1,
                column = 6
            )
        ),
        "{\"a\":\\ " to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 5,
                line = 1,
                column = 6
            )
        ),
        "{\"a\": \"123 " to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 11,
                line = 1,
                column = 12,
                reason = ": expected closing quote for a string value"
            )
        ),
        "{\"a\":}" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '}',
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "{\"a\":]" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ']',
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "{\"a\"::" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "{\"a\":, " to error(
            JsParserErrors.UnexpectedCharacter(
                char = ',',
                position = 5,
                line = 1,
                column = 6,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "{\"a\": tru" to error(
            JsParserErrors.UnrecognizedToken(
                token = "tru",
                position = 6,
                line = 1,
                column = 7,
                reason = ": expected a valid value (String, Number, Array, Object or token 'null', 'true' or 'false')"
            )
        ),
        "{\"a\": true" to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 10,
                line = 1,
                column = 11
            )
        ),
        "{\"a\": true\\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 10,
                line = 1,
                column = 11
            )
        ),
        "{\"a\": true \\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 11,
                line = 1,
                column = 12
            )
        ),
        "{\"a\": true true}" to error(
            JsParserErrors.UnrecognizedToken(
                token = "true",
                position = 11,
                line = 1,
                column = 12,
                reason = ": expected comma to separate Object properties or close marker '}'"
            )
        ),
        "{\"a\": true {" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '{',
                position = 11,
                line = 1,
                column = 12,
                reason = ": expected comma to separate Object properties or close marker '}'"
            )
        ),
        "{\"a\": true [" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '[',
                position = 11,
                line = 1,
                column = 12,
                reason = ": expected comma to separate Object properties or close marker '}'"
            )
        ),
        "{\"a\": true ]" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ']',
                position = 11,
                line = 1,
                column = 12,
                reason = ": expected comma to separate Object properties or close marker '}'"
            )
        ),
        "{\"a\": true :" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 11,
                line = 1,
                column = 12,
                reason = ": expected comma to separate Object properties or close marker '}'"
            )
        ),
        "{\"a\": true," to error(
            JsParserErrors.UnexpectedEndOfInput(
                position = 11,
                line = 1,
                column = 12
            )
        ),
        "{\"a\": true, \\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 12,
                line = 1,
                column = 13
            )
        ),
        "{\"a\": true, true" to error(
            JsParserErrors.UnexpectedCharacter(
                char = 't',
                position = 12,
                line = 1,
                column = 13,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{\"a\": true, }" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '}',
                position = 12,
                line = 1,
                column = 13,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{\"a\": true, {" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '{',
                position = 12,
                line = 1,
                column = 13,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{\"a\": true, [" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '[',
                position = 12,
                line = 1,
                column = 13,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{\"a\": true, ]" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ']',
                position = 12,
                line = 1,
                column = 13,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{\"a\": true, :" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 12,
                line = 1,
                column = 13,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{\"a\": true, ," to error(
            JsParserErrors.UnexpectedCharacter(
                char = ',',
                position = 12,
                line = 1,
                column = 13,
                reason = ": expected double-quote to start property name"
            )
        ),
        "{}\\" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '\\',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "{}true" to error(
            JsParserErrors.UnrecognizedToken(
                token = "true",
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "{}{" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '{',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "{}}" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '}',
                position = 2,
                line = 1,
                column = 3,
            )
        ),
        "{}[" to error(
            JsParserErrors.UnexpectedCharacter(
                char = '[',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "{}]" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ']',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "{}," to error(
            JsParserErrors.UnexpectedCharacter(
                char = ',',
                position = 2,
                line = 1,
                column = 3
            )
        ),
        "{}:" to error(
            JsParserErrors.UnexpectedCharacter(
                char = ':',
                position = 2,
                line = 1,
                column = 3
            )
        )
    )

    private fun error(cause: JsParserErrors): JsParserResult.Failure = JsParserResult.Failure(cause)

    private fun struct(vararg properties: Pair<String, JsValue>): JsValue = JsStruct.invoke(properties.toList())
    private fun array(vararg items: JsValue): JsValue = JsArray.invoke(items.toList())
    private fun stringValue(value: String): JsValue = JsString(value)
    private fun booleanValue(value: Boolean): JsValue = JsBoolean.valueOf(value)
    private fun numberValue(value: String): JsValue = JsNumber.valueOf(value)!!
    private fun nullValue(): JsValue = JsNull

    companion object {

        @JvmStatic
        private fun nameFn(input: String, result: JsParserResult): String {
            val json = input.escape().ifEmpty { EMPTY_TITLE }
            val postfix = if (result is JsParserResult.Failure)
                " -> ${result.error}"
            else
                ""
            return json + postfix
        }

        @JvmStatic
        private fun nameFn(input: String): String = input.escape().ifEmpty { EMPTY_TITLE }

        private const val EMPTY_TITLE = "<empty>"
    }
}
