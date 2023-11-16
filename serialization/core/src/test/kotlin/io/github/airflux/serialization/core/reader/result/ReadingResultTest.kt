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

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.context.JsContext
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.result.ReadingResult.Failure.Companion.merge
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.test.dummy.DummyReaderPredicate
import io.github.airflux.serialization.test.dummy.DummyValidator
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class ReadingResultTest : FreeSpec() {

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ALTERNATIVE_VALUE = "20"

        private val ENV = JsReaderEnv(EB(), Unit)
        private val CONTEXT: JsContext = JsContext
        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The extension function ReadingResult#fold" - {

            "when result is success" - {
                val original: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.fold(ifFailure = { ALTERNATIVE_VALUE }, ifSuccess = { it.value })

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReadingResult<String> =
                    failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the null value" {
                    val result = original.fold(ifFailure = { ALTERNATIVE_VALUE }, ifSuccess = { it.value })

                    result shouldBe ALTERNATIVE_VALUE
                }
            }
        }

        "The extension function ReadingResult#map" - {

            "when result is success" - {
                val original: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a result of applying the [transform] function to the value" {
                    val result = original.map { it.toInt() }

                    result shouldBeSuccess success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
                }
            }

            "when result is failure" - {
                val original: ReadingResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return an original do not apply the [transform] function to a value" {
                    val result = original.map { it.toInt() }

                    result shouldBe original
                }
            }
        }

        "The extension function ReadingResult#bind" - {

            "when result is success" - {
                val original: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a result of applying the [transform] function to the value" {
                    val result = original.bind { location, value ->
                        success(location = location, value = value.toInt())
                    }

                    result shouldBeSuccess success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
                }
            }

            "when result is failure" - {
                val original: ReadingResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return an original do not apply the [transform] function to a value" {
                    val result = original.bind { location, value ->
                        success(location = location, value = value.toInt())
                    }

                    result shouldBe original
                }
            }
        }

        "The extension function ReadingResult#recovery" - {

            "when result is success" - {
                val original: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return an original value" {
                    val result =
                        original.recovery { success(location = LOCATION, value = ALTERNATIVE_VALUE) }

                    result shouldBeSuccess success(location = LOCATION, value = ORIGINAL_VALUE)
                }
            }

            "when result is failure" - {
                val original: ReadingResult<String> =
                    failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the result of invoking the recovery function" {
                    val result =
                        original.recovery { success(location = LOCATION, value = ALTERNATIVE_VALUE) }

                    result shouldBeSuccess success(location = LOCATION, value = ALTERNATIVE_VALUE)
                }
            }
        }

        "The extension function ReadingResult#getOrNull" - {

            "when result is success" - {
                val original: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.getOrNull()

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReadingResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the null value" {
                    val result = original.getOrNull()

                    result.shouldBeNull()
                }
            }
        }

        "The extension function ReadingResult#getOrElse" - {

            "when result is success" - {
                val original: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.getOrElse(ALTERNATIVE_VALUE)

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReadingResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the defaultValue value" {
                    val result = original.getOrElse(ALTERNATIVE_VALUE)

                    result shouldBe ALTERNATIVE_VALUE
                }
            }
        }

        "The extension function ReadingResult#getOrHandle" - {

            "when result is success" - {
                val original: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.getOrHandle { ALTERNATIVE_VALUE }

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReadingResult<String> =
                    failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return a value from a handler" {
                    val result = original.getOrHandle { ALTERNATIVE_VALUE }

                    result shouldBe ALTERNATIVE_VALUE
                }
            }
        }

        "The extension function ReadingResult#orElse" - {

            "when result is success" - {
                val original: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val elseResult: ReadingResult<String> = success(location = LOCATION, value = ALTERNATIVE_VALUE)

                    val result = original.orElse { elseResult }

                    result shouldBe original
                }
            }

            "when result is failure" - {
                val original: ReadingResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return the defaultValue value" {
                    val elseResult: ReadingResult<String> = success(location = LOCATION, value = ALTERNATIVE_VALUE)

                    val result = original.orElse { elseResult }

                    result shouldBe elseResult
                }
            }
        }

        "The extension function ReadingResult#orThrow" - {

            "when result is success" - {
                val original: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                "then should return a value" {
                    val result = original.orThrow { throw IllegalStateException() }

                    result shouldBe ORIGINAL_VALUE
                }
            }

            "when result is failure" - {
                val original: ReadingResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then should return an exception" {
                    shouldThrow<IllegalStateException> {
                        original.orThrow { throw IllegalStateException() }
                    }
                }
            }
        }

        "The extension function ReadingResult#filter" - {

            "when result is success" - {

                "when the value in the result is not null" - {

                    "when the value satisfies the predicate" - {
                        val result: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)
                        val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate(result = true)

                        "then filter should return the original value" {
                            val filtered = result.filter(ENV, CONTEXT, predicate)
                            filtered shouldBeSameInstanceAs result
                        }
                    }

                    "when the value does not satisfy the predicate" - {
                        val result: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)
                        val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate(result = false)

                        "then filter should return null" {
                            val filtered = result.filter(ENV, CONTEXT, predicate)
                            filtered shouldBe success(location = LOCATION, value = null)
                        }
                    }
                }

                "when the value in the result is null" - {
                    val result: ReadingResult<String?> = success(location = LOCATION, value = null)
                    val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate { _, _, _, _ ->
                        throw io.kotest.assertions.failure("Predicate not called.")
                    }

                    "then the filter should not be applying" {
                        val filtered = result.filter(ENV, CONTEXT, predicate)
                        filtered shouldBe result
                    }
                }
            }

            "when result is failure" - {
                val result: ReadingResult<String> = failure(
                    location = LOCATION,
                    error = JsonErrors.InvalidType(
                        expected = listOf(JsString.nameOfType),
                        actual = JsBoolean.nameOfType
                    )
                )
                val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate { _, _, _, _ ->
                    throw io.kotest.assertions.failure("Predicate not called.")
                }

                "then the filter should not be applying" {
                    val filtered = result.filter(ENV, CONTEXT, predicate)
                    filtered shouldBe result
                }
            }
        }

        "The extension function ReadingResult#validation" - {
            val isNotEmpty = DummyValidator.isNotEmptyString<EB, Unit> { JsonErrors.Validation.Strings.IsEmpty }

            "when result is success" - {

                "when the value does not contain a valid value" - {
                    val result: ReadingResult<String> = success(location = LOCATION, value = "")

                    "then validator should return an error" {
                        val validationResult = result.validation(ENV, CONTEXT, isNotEmpty)

                        validationResult shouldBe failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when the value contains a valid value" - {
                    val result: ReadingResult<String> = success(location = LOCATION, value = "user")

                    "then validator should return the original value" {
                        val validationResult = result.validation(ENV, CONTEXT, isNotEmpty)
                        validationResult shouldBe result
                    }
                }
            }

            "when result is failure" - {
                val result: ReadingResult<String> =
                    failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then validator should return the original value" {
                    val validationResult = result.validation(ENV, CONTEXT, isNotEmpty)
                    validationResult shouldBe result
                }
            }
        }

        "The extension function ReadingResult#ifNullValue" - {

            "when result is success" - {

                "when the value is not null" - {
                    val result: ReadingResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then the method should return the original value" {
                        val alternative: ReadingResult<String?> = result.ifNullValue { ALTERNATIVE_VALUE }

                        alternative shouldBeSameInstanceAs result
                    }
                }

                "when the value is null" - {
                    val result: ReadingResult<String?> = success(location = LOCATION, value = null)

                    "then the method should return the default value" {
                        val alternative: ReadingResult<String?> = result.ifNullValue { ALTERNATIVE_VALUE }

                        alternative shouldBeSuccess success(location = LOCATION, value = ALTERNATIVE_VALUE)
                    }
                }
            }

            "when result is failure" - {
                val result: ReadingResult<String> =
                    failure(location = LOCATION, error = JsonErrors.PathMissing)

                "then the method should return the original value" {
                    val alternative: ReadingResult<String> = result.ifNullValue { ALTERNATIVE_VALUE }

                    alternative shouldBeSameInstanceAs result
                }
            }
        }

        "The extension function T#toSuccess" {
            val result = ORIGINAL_VALUE.toSuccess(LOCATION)

            result shouldBeSuccess ReadingResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
        }

        "The extension function E#toFailure" {
            val result = JsonErrors.PathMissing.toFailure(LOCATION)

            result shouldBeFailure ReadingResult.Failure(
                location = LOCATION,
                errors = ReadingResult.Errors(JsonErrors.PathMissing)
            )
        }

        "The extension function ReadingResult#withCatching" - {

            "when no exception is thrown in the block" - {
                val block: () -> ReadingResult<String> = { ORIGINAL_VALUE.toSuccess(LOCATION) }

                "when the context contains the exceptions handler" - {
                    val env = JsReaderEnv(
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

                        result shouldBeSuccess success(location = LOCATION, value = ORIGINAL_VALUE)
                    }
                }
            }

            "when an exception is thrown in the block" - {
                val block: () -> ReadingResult<String> = { throw IllegalStateException() }

                "when the context contains the exceptions handler" - {
                    val env = JsReaderEnv(
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

                        result shouldBeFailure failure(
                            location = LOCATION,
                            error = JsonErrors.PathMissing
                        )
                    }
                }

                "when the context does not contain the exceptions handler" - {
                    val env = JsReaderEnv(Unit, Unit)

                    "then should re-throwing the exception" {
                        shouldThrow<IllegalStateException> {
                            withCatching(env, LOCATION, block)
                        }
                    }
                }
            }
        }

        "The extension function Collection<ReadingResult#Failure>#merge" {
            val failures = listOf(
                ReadingResult.Failure(location = LOCATION, errors = ReadingResult.Errors(JsonErrors.PathMissing)),
                ReadingResult.Failure(
                    location = LOCATION,
                    errors = ReadingResult.Errors(
                        JsonErrors.InvalidType(
                            expected = listOf(JsString.nameOfType),
                            actual = JsBoolean.nameOfType
                        )
                    )
                )
            )

            val failure = failures.merge()

            failure.causes shouldContainExactly listOf(
                ReadingResult.Failure.Cause(location = LOCATION, errors = ReadingResult.Errors(JsonErrors.PathMissing)),
                ReadingResult.Failure.Cause(
                    location = LOCATION,
                    errors = ReadingResult.Errors(
                        JsonErrors.InvalidType(
                            expected = listOf(JsString.nameOfType),
                            actual = JsBoolean.nameOfType
                        )
                    )
                )
            )
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
