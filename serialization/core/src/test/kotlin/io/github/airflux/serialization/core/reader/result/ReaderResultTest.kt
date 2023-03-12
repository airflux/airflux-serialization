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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.core.common.DummyReaderPredicate
import io.github.airflux.serialization.core.common.DummyValidator
import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeFailure
import io.github.airflux.serialization.core.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.predicate.ReaderPredicate
import io.github.airflux.serialization.core.reader.result.ReaderResult.Failure.Companion.merge
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class ReaderResultTest : FreeSpec() {

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ALTERNATIVE_VALUE = "20"

        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
    }

    init {

        "The extension function ReaderResult#fold" - {

            "when result is success" - {
                val original: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.fold(ifFailure = { ALTERNATIVE_VALUE }, ifSuccess = { it.value })

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the null value" {
                    val result = original.fold(ifFailure = { ALTERNATIVE_VALUE }, ifSuccess = { it.value })

                    result shouldBe ALTERNATIVE_VALUE
                }
            }
        }

        "The extension function ReaderResult#map" - {

            "when result is success" - {
                val original: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a result of applying the [transform] function to the value" {
                    val result = original.map { it.toInt() }

                    result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
                }
            }

            "when result is failure" - {
                val original: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return an original do not apply the [transform] function to a value" {
                    val result = original.map { it.toInt() }

                    result shouldBe original
                }
            }
        }

        "The extension function ReaderResult#flatMap" - {

            "when result is success" - {
                val original: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a result of applying the [transform] function to the value" {
                    val result = original.flatMap { location, value ->
                        ReaderResult.Success(location = location, value = value.toInt())
                    }

                    result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
                }
            }

            "when result is failure" - {
                val original: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return an original do not apply the [transform] function to a value" {
                    val result = original.flatMap { location, value ->
                        ReaderResult.Success(location = location, value = value.toInt())
                    }

                    result shouldBe original
                }
            }
        }

        "The extension function ReaderResult#recovery" - {

            "when result is success" - {
                val original: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return an original value" {
                    val result =
                        original.recovery { ReaderResult.Success(location = LOCATION, value = ALTERNATIVE_VALUE) }

                    result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
                }
            }

            "when result is failure" - {
                val original: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the result of invoking the recovery function" {
                    val result =
                        original.recovery { ReaderResult.Success(location = LOCATION, value = ALTERNATIVE_VALUE) }

                    result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = ALTERNATIVE_VALUE)
                }
            }
        }

        "The extension function ReaderResult#getOrNull" - {

            "when result is success" - {
                val original: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.getOrNull()

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the null value" {
                    val result = original.getOrNull()

                    result.shouldBeNull()
                }
            }
        }

        "The extension function ReaderResult#getOrElse" - {

            "when result is success" - {
                val original: ReaderResult<String> =
                    ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.getOrElse { ALTERNATIVE_VALUE }

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the defaultValue value" {
                    val result = original.getOrElse { ALTERNATIVE_VALUE }

                    result shouldBe ALTERNATIVE_VALUE
                }
            }
        }

        "The extension function ReaderResult#getOrHandle" - {

            "when result is success" - {
                val original: ReaderResult<String> =
                    ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.getOrHandle { ALTERNATIVE_VALUE }

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return a value from a handler" {
                    val result = original.getOrHandle { ALTERNATIVE_VALUE }

                    result shouldBe ALTERNATIVE_VALUE
                }
            }
        }

        "The extension function ReaderResult#orElse" - {

            "when result is success" - {
                val original: ReaderResult<String> =
                    ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val elseResult: ReaderResult.Success<String> =
                        ReaderResult.Success(location = LOCATION, value = ALTERNATIVE_VALUE)

                    val result = original.orElse { elseResult }

                    result shouldBe original
                }
            }

            "when result is failure" - {
                val original: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the defaultValue value" {
                    val elseResult: ReaderResult.Success<String> =
                        ReaderResult.Success(location = LOCATION, value = ALTERNATIVE_VALUE)

                    val result = original.orElse { elseResult }

                    result shouldBe elseResult
                }
            }
        }

        "The extension function ReaderResult#orThrow" - {

            "when result is success" - {
                val original: ReaderResult<String> =
                    ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.orThrow { throw IllegalStateException() }

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return an exception" {
                    shouldThrow<IllegalStateException> {
                        original.orThrow { throw IllegalStateException() }
                    }
                }
            }
        }

        "The extension function ReaderResult#filter" - {

            "when result is success" - {

                "when the value in the result is not null" - {

                    "when the value satisfies the predicate" - {
                        val result: ReaderResult<String> =
                            ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
                        val predicate: ReaderPredicate<EB, Unit, Unit, String> = DummyReaderPredicate(result = true)

                        "then filter should return the original value" {
                            val filtered = result.filter(ENV, CONTEXT, predicate)
                            filtered shouldBeSameInstanceAs result
                        }
                    }

                    "when the value does not satisfy the predicate" - {
                        val result: ReaderResult<String> =
                            ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
                        val predicate: ReaderPredicate<EB, Unit, Unit, String> = DummyReaderPredicate(result = false)

                        "then filter should return null" {
                            val filtered = result.filter(ENV, CONTEXT, predicate)
                            filtered shouldBe ReaderResult.Success(location = LOCATION, value = null)
                        }
                    }
                }

                "when the value in the result is null" - {
                    val result: ReaderResult<String?> = ReaderResult.Success(location = LOCATION, value = null)
                    val predicate: ReaderPredicate<EB, Unit, Unit, String> = DummyReaderPredicate { _, _, _, _ ->
                        throw failure("Predicate not called.")
                    }

                    "then the filter should not be applying" {
                        val filtered = result.filter(ENV, CONTEXT, predicate)
                        filtered shouldBe result
                    }
                }
            }

            "when result is failure" - {
                val result: ReaderResult<String> = ReaderResult.Failure(
                    location = LOCATION,
                    error = JsonErrors.InvalidType(
                        expected = listOf(StringNode.nameOfType),
                        actual = BooleanNode.nameOfType
                    )
                )
                val predicate: ReaderPredicate<EB, Unit, Unit, String> = DummyReaderPredicate { _, _, _, _ ->
                    throw failure("Predicate not called.")
                }

                "then the filter should not be applying" {
                    val filtered = result.filter(ENV, CONTEXT, predicate)
                    filtered shouldBe result
                }
            }
        }

        "The extension function ReaderResult#validation" - {
            val isNotEmpty = DummyValidator.isNotEmptyString<EB, Unit, Unit> { JsonErrors.Validation.Strings.IsEmpty }

            "when result is success" - {

                "when the value does not contain a valid value" - {
                    val result: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = "")

                    "then validator should return an error" {
                        val validated = result.validation(
                            ENV,
                            CONTEXT, isNotEmpty
                        )

                        validated shouldBe ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when the value contains a valid value" - {
                    val result: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = "user")

                    "then validator should return the original value" {
                        val validated = result.validation(
                            ENV,
                            CONTEXT, isNotEmpty
                        )
                        validated shouldBe result
                    }
                }
            }

            "when result is failure" - {
                val result: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then validator should return the original value" {
                    val validated = result.validation(ENV, CONTEXT, isNotEmpty)
                    validated shouldBe result
                }
            }
        }

        "The extension function ReaderResult#ifNullValue" - {

            "when result is success" - {

                "when the value is not null" - {
                    val result: ReaderResult<String> = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then the method should return the original value" {
                        val alternative: ReaderResult<String> = result.ifNullValue { _ -> ALTERNATIVE_VALUE }

                        alternative shouldBeSameInstanceAs result
                    }
                }

                "when the value is null" - {
                    val result: ReaderResult<String?> = ReaderResult.Success(location = LOCATION, value = null)

                    "then the method should return the default value" {
                        val alternative: ReaderResult<String> = result.ifNullValue { _ -> ALTERNATIVE_VALUE }

                        alternative shouldBeSuccess ReaderResult.Success(location = LOCATION, value = ALTERNATIVE_VALUE)
                    }
                }
            }

            "when result is failure" - {
                val result: ReaderResult<String> =
                    ReaderResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then the method should return the original value" {
                    val alternative: ReaderResult<String> = result.ifNullValue { _ -> ALTERNATIVE_VALUE }

                    alternative shouldBeSameInstanceAs result
                }
            }
        }

        "The extension function T#success" {
            val result = ORIGINAL_VALUE.success(LOCATION)

            result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
        }

        "The extension function E#failure" {
            val result = JsonErrors.PathMissing.failure(LOCATION)

            result shouldBeFailure ReaderResult.Failure(
                location = LOCATION,
                errors = ReaderResult.Errors(JsonErrors.PathMissing)
            )
        }

        "The extension function ReaderResult#withCatching" - {

            "when no exception is thrown in the block" - {
                val block: () -> ReaderResult<String> = { ORIGINAL_VALUE.success(LOCATION) }

                "when the context contains the exceptions handler" - {
                    val env = ReaderEnv(
                        errorBuilders = Unit,
                        options = Unit,
                        exceptionsHandler = { _, _, exception ->
                            if (exception is IllegalStateException)
                                JsonErrors.PathMissing
                            else
                                throw exception
                        }
                    )

                    "then should return the value" {
                        val result = withCatching(env, LOCATION, block)

                        result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
                    }
                }
            }

            "when an exception is thrown in the block" - {
                val block: () -> ReaderResult<String> = { throw IllegalStateException() }

                "when the context contains the exceptions handler" - {
                    val env = ReaderEnv(
                        errorBuilders = Unit,
                        options = Unit,
                        exceptionsHandler = { _, _, exception ->
                            if (exception is IllegalStateException)
                                JsonErrors.PathMissing
                            else
                                throw exception
                        }
                    )

                    "then should return an error value" {
                        val result = withCatching(env, LOCATION, block)

                        result shouldBeFailure ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.PathMissing
                        )
                    }
                }

                "when the context does not contain the exceptions handler" - {
                    val env = ReaderEnv(Unit, Unit)

                    "then should re-throwing the exception" {
                        shouldThrow<IllegalStateException> {
                            withCatching(env, LOCATION, block)
                        }
                    }
                }
            }
        }

        "The extension function Collection<ReaderResult#Failure>#merge" {
            val failures = listOf(
                ReaderResult.Failure(location = LOCATION, errors = ReaderResult.Errors(JsonErrors.PathMissing)),
                ReaderResult.Failure(
                    location = LOCATION,
                    errors = ReaderResult.Errors(
                        JsonErrors.InvalidType(
                            expected = listOf(StringNode.nameOfType),
                            actual = BooleanNode.nameOfType
                        )
                    )
                )
            )

            val failure = failures.merge()

            failure.causes shouldContainExactly listOf(
                ReaderResult.Failure.Cause(location = LOCATION, errors = ReaderResult.Errors(JsonErrors.PathMissing)),
                ReaderResult.Failure.Cause(
                    location = LOCATION,
                    errors = ReaderResult.Errors(
                        JsonErrors.InvalidType(
                            expected = listOf(StringNode.nameOfType),
                            actual = BooleanNode.nameOfType
                        )
                    )
                )
            )
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
