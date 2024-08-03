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

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class NumberMatcherTest : FreeSpec() {

    init {

        "The NumberMatcher" - {

            "when the text is empty" - {
                NumberMatcher.match("") shouldBe NumberMatcher.Result.NONE
            }

            "when the text is blank" - {
                NumberMatcher.match("  ") shouldBe NumberMatcher.Result.NONE
            }

            "when the text is a number" - {
                withData(
                    nameFn = { (text, result) -> "when text is `$text` then a result should be the `$result`" },
                    listOf(
                        //INTEGER_PART
                        "-0" to NumberMatcher.Result.INTEGER,
                        "+0" to NumberMatcher.Result.INTEGER,
                        "0" to NumberMatcher.Result.INTEGER,
                        "00" to NumberMatcher.Result.INTEGER,
                        "01" to NumberMatcher.Result.INTEGER,
                        "1234567890" to NumberMatcher.Result.INTEGER,

                        //FRACTAL_DIGITAL_PART
                        "0.0" to NumberMatcher.Result.REAL,
                        "0.1" to NumberMatcher.Result.REAL,
                        "00.1" to NumberMatcher.Result.REAL,
                        "01.2" to NumberMatcher.Result.REAL,
                        "001.2" to NumberMatcher.Result.REAL,
                        "1.23" to NumberMatcher.Result.REAL,

                        //EXPONENTIAL_DIGIT_PART
                        "1e2" to NumberMatcher.Result.REAL,
                        "1E2" to NumberMatcher.Result.REAL,

                        "1e23" to NumberMatcher.Result.REAL,

                        "1e-2" to NumberMatcher.Result.REAL,
                        "1e+2" to NumberMatcher.Result.REAL,

                        "1E-2" to NumberMatcher.Result.REAL,
                        "1E+2" to NumberMatcher.Result.REAL,

                        "1e-23" to NumberMatcher.Result.REAL,
                    )
                ) { (token, result) ->
                    NumberMatcher.match(token) shouldBe result
                }
            }

            "when the text is not a number" - {
                withData(
                    nameFn = { text -> "when text is `$text` then a result should be the `NONE`" },
                    listOf(
                        //START
                        "true",
                        ".",

                        //SIGN_PART
                        "-",
                        "+",
                        "-a",
                        "+a",

                        //INTEGER_PART
                        "1a",

                        //FRACTAL_PART
                        "1.",
                        "1..",
                        "1.a",

                        //FRACTAL_DIGITAL_PART
                        "1.2a",

                        //EXPONENTIAL_PART
                        "1e",
                        "1ea",

                        "1E",
                        "1Ea",

                        "1.2e",
                        "1.2ea",

                        "1.2E",
                        "1.2Ea",

                        //EXPONENTIAL_SIGN_PART
                        "1e-",
                        "1e+",
                        "1e-a",
                        "1e+a",

                        "1E-",
                        "1E+",
                        "1E-a",
                        "1E+a",

                        "1.2e-",
                        "1.2e+",
                        "1.2e-a",
                        "1.2e+a",

                        "1.2E-",
                        "1.2E+",
                        "1.2Ea-",
                        "1.2E+a",

                        //EXPONENTIAL_DIGIT_PART
                        "1e2a",
                        "1e-2a",
                        "1e+2a",

                        "1E2a",
                        "1E-2a",
                        "1E+2a",

                        "1.2e3a",
                        "1.2e-3a",
                        "1.2e+3a",

                        "1.2E3a",
                        "1.2E-3a",
                        "1.2E+3a",
                    )
                ) { text ->
                    NumberMatcher.match(text) shouldBe NumberMatcher.Result.NONE
                }
            }
        }
    }
}
