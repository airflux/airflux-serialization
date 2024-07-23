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

        "The JsNumber#Integer type" - {

            "when an instance is created from Byte type" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf(Byte.MIN_VALUE, Byte.MAX_VALUE)
                ) { value ->
                    JsNumber.valueOf(value).get.toByte() shouldBe value
                }
            }

            "when an instance is created from Short type" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf(Short.MIN_VALUE, Short.MAX_VALUE)
                ) { value ->
                    JsNumber.valueOf(value).get.toShort() shouldBe value
                }
            }

            "when an instance is created from Int type" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf(Int.MIN_VALUE, Int.MAX_VALUE)
                ) { value ->
                    JsNumber.valueOf(value).get.toInt() shouldBe value
                }
            }

            "when an instance is created from Long type" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    listOf(Long.MIN_VALUE, Long.MAX_VALUE)
                ) { value ->
                    JsNumber.valueOf(value).get.toLong() shouldBe value
                }
            }

            "when an instance is created from a string representing a integer value" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    integerValues
                ) { value ->
                    JsNumber.Integer.valueOrNullOf(value)?.get shouldBe value
                }
            }

            "when an instance is created from an invalid string" - {
                withData(
                    nameFn = { "with value '$it' should return the null value" },
                    listOf(".0", "+.0", "-.0", "1.5", "-1.5", "1.50", "-1.50")
                ) { value ->
                    JsNumber.Integer.valueOrNullOf(value).shouldBeNull()
                }
            }

            "should comply with equals() and hashCode() contract" {
                JsNumber.valueOf(Byte.MIN_VALUE).shouldBeEqualsContract(
                    y = JsNumber.valueOf(Byte.MIN_VALUE),
                    z = JsNumber.valueOf(Byte.MIN_VALUE),
                    other = JsNumber.valueOf(Byte.MAX_VALUE)
                )
            }

            "then the toString() method should return the expected string" {
                val value = Byte.MIN_VALUE.toString()
                JsNumber.Integer.valueOrNullOf(value)!! shouldBeEqualsString "JsNumber.Integer($value)"
            }
        }

        "The JsNumber#Real type" - {

            "when an instance is created from a string representing a number value" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    numberValues
                ) { value ->
                    JsNumber.Real.valueOrNullOf(value)?.get shouldBe value
                }
            }

            "when an instance is created from an invalid string" - {
                withData(
                    nameFn = { "with value '$it' should return the null value" },
                    listOf("false")
                ) { value ->
                    JsNumber.Real.valueOrNullOf(value)?.get.shouldBeNull()
                }
            }

            "should comply with equals() and hashCode() contract" {
                JsNumber.Real.valueOrNullOf(Byte.MIN_VALUE.toString())!!.shouldBeEqualsContract(
                    y = JsNumber.Real.valueOrNullOf(Byte.MIN_VALUE.toString())!!,
                    z = JsNumber.Real.valueOrNullOf(Byte.MIN_VALUE.toString())!!,
                    other = JsNumber.Real.valueOrNullOf(Byte.MAX_VALUE.toString())!!
                )
            }

            "then the toString() method should return the expected string" {
                val value = Byte.MIN_VALUE.toString()
                JsNumber.Real.valueOrNullOf(value)!! shouldBeEqualsString "JsNumber.Real($value)"
            }
        }

        "The JsNumber type" - {

            "when an instance is created from a string representing a numeric value" - {
                withData(
                    nameFn = { "then the object should contain the passed value $it" },
                    integerValues + numberValues
                ) { value ->
                    JsNumber.valueOrNull(value)?.get shouldBe value
                }
            }

            "when an instance is created from an invalid string" - {
                val value = "abc"

                "then the function should return the null value" {
                    JsNumber.valueOrNull(value).shouldBeNull()
                }
            }
        }
    }

    private companion object {
        private val integerValues = listOf(
            Long.MIN_VALUE.toString(),
            "-1",
            "0",
            "1",
            Long.MAX_VALUE.toString()
        )

        private val numberValues = listOf(
            "-10",
            "-10.5",
            "0.0",
            "10",
            "10.5"
        )
    }
}
