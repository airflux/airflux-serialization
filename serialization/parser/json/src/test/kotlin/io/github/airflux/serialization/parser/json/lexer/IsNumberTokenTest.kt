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

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class IsNumberTokenTest : FreeSpec() {

    init {

        "The isNumberToken extension function" - {
            withData(
                nameFn = { (token, result) -> "The token '$token' ${if (result) "is" else "is not"} a number token." },
                numberCaseData() + notNumberCaseData()
            ) { (token, result) ->
                token.isNumberToken() shouldBe result
            }
        }
    }

    private fun numberCaseData() = listOf(
        "-0" to true,
        "0" to true,
        "+0" to true,

        "1234567890" to true,
        "1.1234567890" to true,
        "1.2e3" to true,
        "0.0" to true,

        "1.2e-34" to true,
        "1.2e+34" to true,
        "1.2e34" to true,

        "1.2E-34" to true,
        "1.2E+34" to true,
        "1.2E34" to true
    )

    private fun notNumberCaseData() = listOf(
        "true" to false,

        "-" to false,
        "+" to false,

        "--1.2e-3" to false,
        "+-1.2e-3" to false,
        "-+1.2e-3" to false,
        "++1.2e-3" to false,

        "-.2e-3" to false,
        "+.2e-3" to false,

        ".2e-1" to false,
        ".2e+1" to false,

        "1.e-1" to false,
        "1.e+1" to false,

        "1.2-1" to false,
        "1.2+1" to false,

        "1.2a-1" to false,
        "1.2a+1" to false,

        "1e" to false,
        "1E" to false,

        "1.2e" to false,
        "1.2E" to false,

        "1.2e-" to false,
        "1.2e+" to false,
        "1.2ee" to false,

        "1.2e+e" to false,

        "1.2e-23e" to false,
        "1a" to false,
    )
}
