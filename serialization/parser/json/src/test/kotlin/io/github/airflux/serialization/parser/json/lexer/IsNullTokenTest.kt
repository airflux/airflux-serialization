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

internal class IsNullTokenTest : FreeSpec() {

    init {

        "The isNullToken extension function" - {
            withData(
                nameFn = { (token, result) -> "The token '$token' ${if (result) "is" else "is not"} a null token." },
                successCaseData() + failureCaseData()
            ) { (token, result) ->
                token.isNullToken() shouldBe result
            }
        }
    }

    private fun successCaseData() = listOf(
        "null" to true,
        "NULL" to true,
        "Null" to true,
        "NuLl" to true,
    )

    private fun failureCaseData() = listOf(
        "true" to false,
        "TRUE" to false,
        "True" to false,
        "TrUe" to false,
    )
}
