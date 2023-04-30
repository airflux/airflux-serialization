/*
 * Copyright 2021-2023 Maxim Sambulat.
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

import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsNumericTest : FreeSpec() {

    init {

        "The JsNumeric#Integer type" - {

            "when the object creating from Byte type" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf(Byte.MIN_VALUE, Byte.MAX_VALUE)
                ) { value ->
                    JsNumeric.valueOf(value).get.toByte() shouldBe value
                }
            }

            "when the object creating from Short type" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf(Short.MIN_VALUE, Short.MAX_VALUE)
                ) { value ->
                    JsNumeric.valueOf(value).get.toShort() shouldBe value
                }
            }

            "when the object creating from Int type" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf(Int.MIN_VALUE, Int.MAX_VALUE)
                ) { value ->
                    JsNumeric.valueOf(value).get.toInt() shouldBe value
                }
            }

            "when the object creating from Long type" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf(Long.MIN_VALUE, Long.MAX_VALUE)
                ) { value ->
                    JsNumeric.valueOf(value).get.toLong() shouldBe value
                }
            }

            "when the object creating from a valid string representing a numeric value" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf(Long.MIN_VALUE.toString(), "-1", "1", Long.MAX_VALUE.toString())
                ) { value ->
                    JsNumeric.Integer.valueOrNullOf(value)?.get shouldBe value
                }
            }

            "when an object creating from an invalid string" - {
                withData(
                    nameFn = { "with value '$it' should return the null value" },
                    listOf(".0", "+.0", "-.0", "1.5", "-1.5", "1.50", "-1.50")
                ) { value ->
                    JsNumeric.Integer.valueOrNullOf(value).shouldBeNull()
                }
            }

            "should comply with equals() and hashCode() contract" {
                JsNumeric.valueOf(Byte.MIN_VALUE).shouldBeEqualsContract(
                    y = JsNumeric.valueOf(Byte.MIN_VALUE),
                    z = JsNumeric.valueOf(Byte.MIN_VALUE),
                    other = JsNumeric.valueOf(Byte.MAX_VALUE)
                )
            }

            "then the toString() method should return the expected string" {
                val value = Byte.MIN_VALUE.toString()
                JsNumeric.Integer.valueOrNullOf(value)!! shouldBeEqualsString value
            }
        }

        "The JsNumeric#Number type" - {

            "when an object creating from a valid string representing a numeric value" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf("-10", "-10.5", "10", "10.5")
                ) { value ->
                    JsNumeric.Number.valueOrNullOf(value)?.get shouldBe value
                }
            }

            "when an object creating from an invalid string" - {
                withData(
                    nameFn = { "with value '$it' should return the null value" },
                    listOf("false")
                ) { value ->
                    JsNumeric.Number.valueOrNullOf(value)?.get.shouldBeNull()
                }
            }

            "should comply with equals() and hashCode() contract" {
                JsNumeric.Number.valueOrNullOf(Byte.MIN_VALUE.toString())!!.shouldBeEqualsContract(
                    y = JsNumeric.Number.valueOrNullOf(Byte.MIN_VALUE.toString())!!,
                    z = JsNumeric.Number.valueOrNullOf(Byte.MIN_VALUE.toString())!!,
                    other = JsNumeric.Number.valueOrNullOf(Byte.MAX_VALUE.toString())!!
                )
            }

            "then the toString() method should return the expected string" {
                val value = Byte.MIN_VALUE.toString()
                JsNumeric.Number.valueOrNullOf(value)!! shouldBeEqualsString value
            }
        }
    }
}
