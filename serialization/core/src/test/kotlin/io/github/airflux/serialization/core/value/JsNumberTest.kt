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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.test.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.test.kotest.shouldBeEqualsString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsNumberTest : FreeSpec() {

    init {

        "The JsNumber type" - {

            "when an instance is created from a string that is a valid numeric value" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    integerNumbers + realNumbers
                ) { (value, subType) ->
                    val number = JsNumber.valueOf(value)!!
                    number.get shouldBe value
                    number.subType shouldBe subType
                }
            }

            "when an instance is created from a string that is not a numeric value" - {
                withData(
                    nameFn = { "then should return the null value for the string `$it`" },
                    listOf("abc", ".0", "+.0", "-.0")
                ) { value ->
                    JsNumber.valueOf(value).shouldBeNull()
                }
            }

            "should comply with equals() and hashCode() contract" {
                JsNumber.valueOf(Byte.MIN_VALUE.toString())!!.shouldBeEqualsContract(
                    y = JsNumber.valueOf(Byte.MIN_VALUE.toString())!!,
                    z = JsNumber.valueOf(Byte.MIN_VALUE.toString())!!,
                    other = JsNumber.valueOf(Byte.MAX_VALUE.toString())!!
                )
            }

            "then the toString() method should return the expected string" {
                val value = Byte.MIN_VALUE.toString()
                JsNumber.valueOf(value)!! shouldBeEqualsString "JsNumber($value)"
            }
        }
    }

    private companion object {

        private val integerNumbers = listOf(
            Long.MIN_VALUE.toString(),
            "-1",
            "0",
            "1",
            Long.MAX_VALUE.toString()
        ).map { it to JsNumber.SubType.INTEGER }

        private val realNumbers = listOf(
            "-10.5",
            "0.0",
            "10.5",
            "1e234"
        ).map { it to JsNumber.SubType.REAL }
    }
}
