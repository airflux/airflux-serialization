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

package io.github.airflux.serialization.parser.common

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class SerializedTest : FreeSpec() {

    init {

        "The Serialized type" - {

            "when the underlying value is null" - {
                val serialized = Serialized(null)

                "then the function `isNone` should return true" {
                    serialized.isNone() shouldBe true
                }

                "then the function `orNull` should return null" {
                    serialized.orNull() shouldBe null
                }

                "then the function `orThrow` should throw an exception" {
                    shouldThrow<IllegalStateException> {
                        serialized.orThrow()
                    }
                }

                "then the function `orThrow` with a custom exception should throw an exception" {
                    shouldThrow<IllegalArgumentException> {
                        serialized.orThrow { IllegalArgumentException() }
                    }
                }

                "then the function `orEmptyArray` should return `[]`" {
                    serialized.orEmptyArray() shouldBe "[]"
                }

                "then the function `orEmptyStruct` should return `{}`" {
                    serialized.orEmptyStruct() shouldBe "{}"
                }
            }

            "when the underlying value is not null" - {
                val serialized = Serialized(JSON)

                "then the function `isNone` should return false" {
                    serialized.isNone() shouldBe false
                }

                "then the function `orNull` should return an underlying value" {
                    serialized.orNull() shouldBe JSON
                }

                "then the function `orThrow` should return an underlying value" {
                    serialized.orThrow() shouldBe JSON
                }

                "then the function `orThrow` with a custom exception should throw an exception" {
                    serialized.orThrow { IllegalArgumentException() } shouldBe JSON
                }

                "then the function `orEmptyArray` should return an underlying value" {
                    serialized.orEmptyArray() shouldBe JSON
                }

                "then the function `orEmptyStruct` should return an underlying value" {
                    serialized.orEmptyStruct() shouldBe JSON
                }
            }
        }
    }

    private companion object {
        private const val JSON = """{"key": "value"}"""
    }
}
