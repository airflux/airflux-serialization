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
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.dsl.reader.context.exception.ExceptionsHandler
import io.github.airflux.serialization.dsl.reader.context.exception.exception
import io.github.airflux.serialization.dsl.reader.context.exception.exceptionsHandler
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ReaderResultTest : FreeSpec() {

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ELSE_VALUE = "20"
        private val CONTEXT = ReaderContext()
        private val LOCATION = Location.empty.append("id")
    }

    init {

        "A ReaderResult#Success type" - {
            val original: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

            "calling fold function should return an original value" {
                val result = original.fold(
                    ifFailure = { ELSE_VALUE },
                    ifSuccess = { it.value }
                )

                result shouldBe ORIGINAL_VALUE
            }

            "calling map function should return a result of applying the [transform] function to the value" {
                val result = original.map { it.toInt() }

                result shouldBe ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
            }

            "calling flatMap function should return a result of applying the [transform] function to the value" {
                val result = original.flatMap { location, value -> ReaderResult.Success(location, value.toInt()) }

                result shouldBe ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
            }

            "calling recovery function should return an original" {
                val result = original.recovery { ReaderResult.Success(LOCATION, ELSE_VALUE) }

                result shouldBe ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
            }

            "calling getOrNull function should return a value" {
                val result = original.getOrNull()

                result shouldBe ORIGINAL_VALUE
            }

            "calling getOrElse function should return a value" {
                val result = original.getOrElse { ELSE_VALUE }

                result shouldBe ORIGINAL_VALUE
            }

            "calling orElse function should return a value" {
                val elseResult = ReaderResult.Success(location = LOCATION, value = ELSE_VALUE)

                val result = original.orElse { elseResult }

                result shouldBe original
            }

            "calling orThrow function should return a value" {
                val result = original.orThrow { throw IllegalStateException() }

                result shouldBe ORIGINAL_VALUE
            }

            "should comply with equals() and hashCode() contract" {
                original.shouldBeEqualsContract(
                    y = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE),
                    z = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE),
                    other = ReaderResult.Success(location = Location.empty, value = ORIGINAL_VALUE)
                )
            }
        }

        "A ReaderResult#Failure type" - {
            val original: ReaderResult<String> =
                ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

            "constructor(JsLocation, JsError)" {
                val failure = ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                failure.causes shouldContainAll listOf(
                    ReaderResult.Failure.Cause(
                        location = LOCATION,
                        errors = ReaderResult.Errors(JsonErrors.PathMissing)
                    )
                )
            }

            "constructor(JsLocation, ReaderResult#Errors)" {
                val errors = ReaderResult.Errors(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                )

                val failure = ReaderResult.Failure(location = LOCATION, errors = errors)

                failure.causes shouldContainAll listOf(ReaderResult.Failure.Cause(location = LOCATION, errors = errors))
            }

            "calling plus function should return " {
                val firstFailure =
                    ReaderResult.Failure(location = LOCATION, errors = ReaderResult.Errors(JsonErrors.PathMissing))
                val secondFailure = ReaderResult.Failure(
                    location = LOCATION,
                    errors = ReaderResult.Errors(
                        JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                    )
                )

                val failure = firstFailure + secondFailure

                failure.causes shouldContainAll listOf(
                    ReaderResult.Failure.Cause(
                        location = LOCATION,
                        errors = ReaderResult.Errors(JsonErrors.PathMissing)
                    ),
                    ReaderResult.Failure.Cause(
                        location = LOCATION,
                        errors = ReaderResult.Errors(
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
                val result = original.flatMap { location, value -> ReaderResult.Success(location, value.toInt()) }

                result shouldBe original
            }

            "calling recovery function should return the result of invoking the recovery function" {
                val result = original.recovery { ReaderResult.Success(LOCATION, ELSE_VALUE) }

                result shouldBe ReaderResult.Success(location = LOCATION, value = ELSE_VALUE)
            }

            "calling getOrNull function should return the null value" {
                val result = original.getOrNull()

                result.shouldBeNull()
            }

            "calling getOrElse function should return a defaultValue" {
                val result = original.getOrElse { ELSE_VALUE }

                result shouldBe ELSE_VALUE
            }

            "calling orElse function should return the result of calling the [defaultValue] function" {
                val elseResult = ReaderResult.Success(location = LOCATION, value = ELSE_VALUE)

                val result = original.orElse { elseResult }

                result shouldBe elseResult
            }

            "calling orThrow function should return an exception" {
                shouldThrow<IllegalStateException> {
                    original.orThrow { throw IllegalStateException() }
                }
            }

            "should comply with equals() and hashCode() contract" {
                original.shouldBeEqualsContract(
                    y = ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    z = ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing),
                    other = ReaderResult.Failure(location = Location.empty, error = JsonErrors.PathMissing)
                )
            }
        }

        "A ReaderResult#Failure#Cause type" - {

            "constructor(JsLocation, JsError)" {
                val cause = ReaderResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing)

                cause.location shouldBe LOCATION
                cause.errors shouldBe ReaderResult.Errors(JsonErrors.PathMissing)
            }

            "constructor(Location, ReaderResult#Errors)" {
                val cause = ReaderResult.Failure.Cause(
                    location = LOCATION,
                    errors = ReaderResult.Errors(
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

        "ReaderResult#merge function" {
            val failures = listOf(
                ReaderResult.Failure(location = LOCATION, errors = ReaderResult.Errors(JsonErrors.PathMissing)),
                ReaderResult.Failure(
                    location = LOCATION,
                    errors = ReaderResult.Errors(
                        JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                    )
                )
            )

            val failure = failures.merge()

            failure.causes shouldContainAll listOf(
                ReaderResult.Failure.Cause(location = LOCATION, errors = ReaderResult.Errors(JsonErrors.PathMissing)),
                ReaderResult.Failure.Cause(
                    location = LOCATION,
                    errors = ReaderResult.Errors(
                        JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                    )
                )
            )
        }

        "ReaderResult#withCatching" - {

            "when no exception is thrown in the block" - {
                val block: () -> ReaderResult<String> = { ORIGINAL_VALUE.success(LOCATION) }

                "then should return the value" {
                    val result = withCatching(CONTEXT, LOCATION, block)

                    result as ReaderResult.Success
                    result.value shouldBe ORIGINAL_VALUE
                }
            }

            "when an exception is thrown in the block" - {
                val block: () -> ReaderResult<String> = { throw IllegalStateException() }

                "when the context contains the exceptions handler" - {
                    val exceptionHandler: ExceptionsHandler = exceptionsHandler(
                        exception<IllegalStateException> { _, _, _ ->
                            JsonErrors.PathMissing
                        }
                    )
                    val contextWithExceptionHandler = CONTEXT + exceptionHandler

                    "then should return an error value" {
                        val result = withCatching(contextWithExceptionHandler, LOCATION, block)

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing)
                        )
                    }
                }

                "when the context does not contain the exceptions handler" - {

                    "then should re-throwing the exception" {
                        shouldThrow<IllegalStateException> {
                            withCatching(CONTEXT, LOCATION, block)
                        }
                    }
                }
            }
        }

        "asSuccess(JsLocation) extension function" {
            val result = ORIGINAL_VALUE.success(LOCATION)

            result shouldBe ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
        }

        "asFailure(JsLocation) extension function" {
            val result = JsonErrors.PathMissing.failure(LOCATION)

            result as ReaderResult.Failure

            result.causes shouldContainAll listOf(
                ReaderResult.Failure.Cause(location = LOCATION, errors = ReaderResult.Errors(JsonErrors.PathMissing))
            )
        }
    }
}
