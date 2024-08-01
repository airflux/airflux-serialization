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
                        "-0" to NumberMatcher.Result.INTEGER,
                        "0" to NumberMatcher.Result.INTEGER,
                        "+0" to NumberMatcher.Result.INTEGER,

                        "1234567890" to NumberMatcher.Result.INTEGER,
                        "1.1234567890" to NumberMatcher.Result.REAL,
                        "1.2e3" to NumberMatcher.Result.REAL,
                        "0.0" to NumberMatcher.Result.REAL,

                        "1.2e-34" to NumberMatcher.Result.REAL,
                        "1.2e+34" to NumberMatcher.Result.REAL,
                        "1.2e34" to NumberMatcher.Result.REAL,

                        "1.2E-34" to NumberMatcher.Result.REAL,
                        "1.2E+34" to NumberMatcher.Result.REAL,
                        "1.2E34" to NumberMatcher.Result.REAL
                    )
                ) { (token, result) ->
                    NumberMatcher.match(token) shouldBe result
                }
            }

            "when the text is not a number" - {
                withData(
                    nameFn = { text -> "when text is `$text` then a result should be the `NONE`" },
                    listOf(
                        "true",
                        "-",
                        "+",
                        "--1.2e-3",
                        "+-1.2e-3",
                        "-+1.2e-3",
                        "++1.2e-3",
                        "1.",
                        "-.2e-3",
                        "+.2e-3",
                        ".2e-1",
                        ".2e+1",
                        "1.e-1",
                        "1.e+1",
                        "1.2-1",
                        "1.2+1",
                        "1.2a-1",
                        "1.2a+1",
                        "1e",
                        "1E",
                        "1.2e",
                        "1.2E",
                        "1.2e-",
                        "1.2e+",
                        "1.2ee",
                        "1.2e+e",
                        "1.2e-23e",
                        "1a"
                    )
                ) { text ->
                    NumberMatcher.match(text) shouldBe NumberMatcher.Result.NONE
                }
            }
        }
    }
}
