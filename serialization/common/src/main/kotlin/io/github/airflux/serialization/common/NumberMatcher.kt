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

package io.github.airflux.serialization.common

@Suppress("TooManyFunctions")
public object NumberMatcher {

    public fun match(text: CharSequence): Result {
        var state: State = State.START
        for (pos in text.indices) {
            val char = text[pos]
            val newState = when (state) {
                State.START -> start(char)
                State.SIGN_PART -> signPart(char)
                State.INTEGER_PART -> integerPart(char)
                State.FRACTAL_PART -> fractalPart(char)
                State.FRACTAL_DIGITAL_PART -> fractalDigitalPart(char)
                State.EXPONENTIAL_PART -> exponentialPart(char)
                State.EXPONENTIAL_SIGN_PART -> exponentialSignPart(char)
                State.EXPONENTIAL_DIGIT_PART -> exponentialDigitPart(char)
            }

            if (newState != null) state = newState else return Result.NONE
        }

        return when (state) {
            State.INTEGER_PART -> Result.INTEGER

            State.FRACTAL_DIGITAL_PART,
            State.EXPONENTIAL_DIGIT_PART -> Result.REAL

            else -> Result.NONE
        }
    }

    private fun start(char: Char) =
        if (char.isSignChar())
            State.SIGN_PART
        else if (char.isDigit())
            State.INTEGER_PART
        else
            null

    private fun signPart(char: Char) =
        if (char.isDigit())
            State.INTEGER_PART
        else
            null

    private fun integerPart(char: Char) =
        if (char.isDigit())
            State.INTEGER_PART
        else if (char.isFractalDelimiter())
            State.FRACTAL_PART
        else if (char.isExponentialChar())
            State.EXPONENTIAL_PART
        else
            null

    private fun fractalPart(char: Char) =
        if (char.isDigit())
            State.FRACTAL_DIGITAL_PART
        else
            null

    private fun fractalDigitalPart(char: Char) =
        if (char.isDigit())
            State.FRACTAL_DIGITAL_PART
        else if (char.isExponentialChar())
            State.EXPONENTIAL_PART
        else
            null

    private fun exponentialPart(char: Char) =
        if (char.isSignChar())
            State.EXPONENTIAL_SIGN_PART
        else if (char.isDigit())
            State.EXPONENTIAL_DIGIT_PART
        else
            null

    private fun exponentialSignPart(char: Char) =
        if (char.isDigit())
            State.EXPONENTIAL_DIGIT_PART
        else
            null

    private fun exponentialDigitPart(char: Char) =
        if (char.isDigit())
            State.EXPONENTIAL_DIGIT_PART
        else
            null

    private fun Char.isSignChar() = this == '+' || this == '-'
    private fun Char.isExponentialChar() = this == 'e' || this == 'E'
    private fun Char.isFractalDelimiter() = this == '.'

    private enum class State {
        START,
        SIGN_PART,
        INTEGER_PART,
        FRACTAL_PART,
        FRACTAL_DIGITAL_PART,
        EXPONENTIAL_PART,
        EXPONENTIAL_SIGN_PART,
        EXPONENTIAL_DIGIT_PART
    }

    public enum class Result {
        INTEGER,
        REAL,
        NONE
    }
}
