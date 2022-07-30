/*
 * Copyright 2021-2022 Maxim Sambulat.
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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

internal class JsResultTest : FreeSpec() {

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ELSE_VALUE = "20"
        private val LOCATION = Location.empty.append("id")
    }

    init {

        "A JsResult#Success type" - {
            val original: JsResult<String> = JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

            "calling fold function should return an original value" {
                val result = original.fold(
                    ifFailure = { ELSE_VALUE },
                    ifSuccess = { it.value }
                )

                result shouldBe ORIGINAL_VALUE
            }

            "calling map function should return a result of applying the [transform] function to the value" {
                val result = original.map { it.toInt() }

                result shouldBe JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
            }

            "calling flatMap function should return a result of applying the [transform] function to the value" {
                val result = original.flatMap { location, value -> JsResult.Success(location, value.toInt()) }

                result shouldBe JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
            }

            "calling recovery function should return an original" {
                val result = original.recovery { JsResult.Success(LOCATION, ELSE_VALUE) }

                result shouldBe JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
            }

            "calling getOrElse function should return a value" {
                val result = original.getOrElse { ELSE_VALUE }

                result shouldBe ORIGINAL_VALUE
            }

            "calling orElse function should return a value" {
                val elseResult = JsResult.Success(location = LOCATION, value = ELSE_VALUE)

                val result = original.orElse { elseResult }

                result shouldBe original
            }

            "should comply with equals() and hashCode() contract" {
                original.shouldBeEqualsContract(
                    y = JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE),
                    z = JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE),
                    other = JsResult.Success(location = Location.empty, value = ORIGINAL_VALUE)
                )
            }
        }

        "A JsResult#Failure type" - {
            val original: JsResult<String> = JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

            "constructor(JsLocation, JsError)" {
                val failure = JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                failure.causes shouldContainAll listOf(
                    JsResult.Failure.Cause(location = LOCATION, errors = JsResult.Errors(JsonErrors.PathMissing))
                )
            }

            "constructor(JsLocation, JsResult.Errors)" {
                val errors = JsResult.Errors(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                )

                val failure = JsResult.Failure(location = LOCATION, errors = errors)

                failure.causes shouldContainAll listOf(JsResult.Failure.Cause(location = LOCATION, errors = errors))
            }

            "calling plus function should return " {
                val firstFailure =
                    JsResult.Failure(location = LOCATION, errors = JsResult.Errors(JsonErrors.PathMissing))
                val secondFailure = JsResult.Failure(
                    location = LOCATION,
                    errors = JsResult.Errors(
                        JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                    )
                )

                val failure = firstFailure + secondFailure

                failure.causes shouldContainAll listOf(
                    JsResult.Failure.Cause(location = LOCATION, errors = JsResult.Errors(JsonErrors.PathMissing)),
                    JsResult.Failure.Cause(
                        location = LOCATION,
                        errors = JsResult.Errors(
                            JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                        )
                    )
                )
            }

            "calling fold function should return an alternative value" {
                val result = original.fold(
                    ifFailure = { ELSE_VALUE },
                    ifSuccess = { it.value }
                )

                result shouldBe ELSE_VALUE
            }

            "calling map function should return an original do not apply the [transform] function to the value" {
                val result = original.map { it.toInt() }

                result shouldBe original
            }

            "calling flatMap function should return an original do not apply the [transform] function to the value" {
                val result = original.flatMap { location, value -> JsResult.Success(location, value.toInt()) }

                result shouldBe original
            }

            "calling recovery function should return the result of invoking the recovery function" {
                val result = original.recovery { JsResult.Success(LOCATION, ELSE_VALUE) }

                result shouldBe JsResult.Success(location = LOCATION, value = ELSE_VALUE)
            }

            "calling getOrElse function should return a defaultValue" {
                val result = original.getOrElse { ELSE_VALUE }

                result shouldBe ELSE_VALUE
            }

            "calling orElse function should return the result of calling the [defaultValue] function" {
                val elseResult = JsResult.Success(location = LOCATION, value = ELSE_VALUE)

                val result = original.orElse { elseResult }

                result shouldBe elseResult
            }

            "should comply with equals() and hashCode() contract" {
                original.shouldBeEqualsContract(
                    y = JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    z = JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    other = JsResult.Failure(location = Location.empty, error = JsonErrors.PathMissing)
                )
            }
        }

        "A JsResult#Failure#Cause type" - {

            "constructor(JsLocation, JsError)" {
                val cause = JsResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing)

                cause.location shouldBe LOCATION
                cause.errors shouldBe JsResult.Errors(JsonErrors.PathMissing)
            }

            "constructor(Location, JsResult#Errors)" {
                val cause = JsResult.Failure.Cause(
                    location = LOCATION,
                    errors = JsResult.Errors(
                        JsonErrors.PathMissing,
                        JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                    )
                )

                cause.location shouldBe LOCATION
                cause.errors.items shouldContainAll listOf(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                )
            }
        }

        "JsResult#merge function" {
            val failures = listOf(
                JsResult.Failure(location = LOCATION, errors = JsResult.Errors(JsonErrors.PathMissing)),
                JsResult.Failure(
                    location = LOCATION,
                    errors = JsResult.Errors(
                        JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                    )
                )
            )

            val failure = failures.merge()

            failure.causes shouldContainAll listOf(
                JsResult.Failure.Cause(location = LOCATION, errors = JsResult.Errors(JsonErrors.PathMissing)),
                JsResult.Failure.Cause(
                    location = LOCATION,
                    errors = JsResult.Errors(
                        JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                    )
                )
            )
        }

        "asSuccess(JsLocation) extension function" {
            val result = ORIGINAL_VALUE.success(LOCATION)

            result shouldBe JsResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
        }

        "asFailure(JsLocation) extension function" {
            val result = JsonErrors.PathMissing.failure(LOCATION)

            result as JsResult.Failure

            result.causes shouldContainAll listOf(
                JsResult.Failure.Cause(location = LOCATION, errors = JsResult.Errors(JsonErrors.PathMissing))
            )
        }
    }
}
