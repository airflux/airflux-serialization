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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.test.dummy.DummyReaderPredicate
import io.github.airflux.serialization.test.dummy.DummyValidator
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class JsReaderResultTest : FreeSpec() {

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ALTERNATIVE_VALUE = "20"

        private val ENV = JsReaderEnv(EB(), Unit)
        private val LOCATION: JsLocation = JsLocation
    }

    init {

        "The `JsReaderResult` type" - {

            "the extension function `isSuccess`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return the true" {
                        original.isSuccess() shouldBe true
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> =
                        failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return the false" {
                        original.isSuccess() shouldBe false
                    }
                }
            }

            "the extension function `isError`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return the false" {
                        original.isError() shouldBe false
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> =
                        failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return the true" {
                        original.isError() shouldBe true
                    }
                }
            }

            "the extension function `fold`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return a value" {
                        val result = original.fold(onFailure = { ALTERNATIVE_VALUE }, onSuccess = { it.value })

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> =
                        failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return the null value" {
                        val result = original.fold(onFailure = { ALTERNATIVE_VALUE }, onSuccess = { it.value })

                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the extension function `map`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return a result of applying the [transform] function to the value" {
                        val result = original.map { it.toInt() }

                        result shouldBeSuccess success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return an original do not apply the [transform] function to a value" {
                        val result = original.map { it.toInt() }

                        result shouldBe original
                    }
                }
            }

            "the extension function `bind`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return a result of applying the [transform] function to the value" {
                        val result = original.bind { location, value ->
                            success(location = location, value = value.toInt())
                        }

                        result shouldBeSuccess success(location = LOCATION, value = ORIGINAL_VALUE.toInt())
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return an original do not apply the [transform] function to a value" {
                        val result = original.bind { location, value ->
                            success(location = location, value = value.toInt())
                        }

                        result shouldBe original
                    }
                }
            }

            "the extension function `recovery`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return an original value" {
                        val result =
                            original.recovery { success(location = LOCATION, value = ALTERNATIVE_VALUE) }

                        result shouldBeSuccess success(location = LOCATION, value = ORIGINAL_VALUE)
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> =
                        failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return the result of invoking the recovery function" {
                        val result =
                            original.recovery { success(location = LOCATION, value = ALTERNATIVE_VALUE) }

                        result shouldBeSuccess success(location = LOCATION, value = ALTERNATIVE_VALUE)
                    }
                }
            }

            "the extension function `getOrNull`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return a value" {
                        val result = original.getOrNull()

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return the null value" {
                        val result = original.getOrNull()

                        result.shouldBeNull()
                    }
                }
            }

            "the extension function `getOrElse`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return a value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return the defaultValue value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)

                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the extension function `getOrElse` with lambda" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return a value" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> =
                        failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return a value from a handler" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }

                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the extension function `orElse`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return a value" {
                        val elseResult: JsReaderResult<String> = success(location = LOCATION, value = ALTERNATIVE_VALUE)

                        val result = original.orElse { elseResult }

                        result shouldBe original
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return the defaultValue value" {
                        val elseResult: JsReaderResult<String> = success(location = LOCATION, value = ALTERNATIVE_VALUE)

                        val result = original.orElse { elseResult }

                        result shouldBe elseResult
                    }
                }
            }

            "the extension function `orThrow`" - {

                "when result is success" - {
                    val original: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                    "then should return a value" {
                        val result = original.orThrow { throw IllegalStateException() }

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when result is failure" - {
                    val original: JsReaderResult<String> = failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.orThrow { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the extension function `filter`" - {

                "when result is success" - {

                    "when the value in the result is not null" - {

                        "when the value satisfies the predicate" - {
                            val result: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)
                            val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate(result = true)

                            "then filter should return the original value" {
                                val filtered = result.filter(ENV, predicate)
                                filtered shouldBeSameInstanceAs result
                            }
                        }

                        "when the value does not satisfy the predicate" - {
                            val result: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)
                            val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate(result = false)

                            "then filter should return null" {
                                val filtered = result.filter(ENV, predicate)
                                filtered shouldBe success(location = LOCATION, value = null)
                            }
                        }
                    }

                    "when the value in the result is null" - {
                        val result: JsReaderResult<String?> = success(location = LOCATION, value = null)
                        val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate { _, _, _ ->
                            throw io.kotest.assertions.failure("Predicate not called.")
                        }

                        "then the filter should not be applying" {
                            val filtered = result.filter(ENV, predicate)
                            filtered shouldBe result
                        }
                    }
                }

                "when result is failure" - {
                    val result: JsReaderResult<String> = failure(
                        location = LOCATION,
                        error = JsonErrors.InvalidType(
                            expected = listOf(JsString.nameOfType),
                            actual = JsBoolean.nameOfType
                        )
                    )
                    val predicate: JsPredicate<EB, Unit, String> = DummyReaderPredicate { _, _, _ ->
                        throw io.kotest.assertions.failure("Predicate not called.")
                    }

                    "then the filter should not be applying" {
                        val filtered = result.filter(ENV, predicate)
                        filtered shouldBe result
                    }
                }
            }

            "the extension function `validation`" - {
                val isNotEmpty = DummyValidator.isNotEmptyString<EB, Unit> { JsonErrors.Validation.Strings.IsEmpty }

                "when result is success" - {

                    "when the value does not contain a valid value" - {
                        val result: JsReaderResult<String> = success(location = LOCATION, value = "")

                        "then validator should return an error" {
                            val validationResult = result.validation(ENV, isNotEmpty)

                            validationResult shouldBe failure(
                                location = LOCATION,
                                error = JsonErrors.Validation.Strings.IsEmpty
                            )
                        }
                    }

                    "when the value contains a valid value" - {
                        val result: JsReaderResult<String> = success(location = LOCATION, value = "user")

                        "then validator should return the original value" {
                            val validationResult = result.validation(ENV, isNotEmpty)
                            validationResult shouldBe result
                        }
                    }
                }

                "when result is failure" - {
                    val result: JsReaderResult<String> =
                        failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then validator should return the original value" {
                        val validationResult = result.validation(ENV, isNotEmpty)
                        validationResult shouldBe result
                    }
                }
            }

            "the extension function `ifNullValue`" - {

                "when result is success" - {

                    "when the value is not null" - {
                        val result: JsReaderResult<String> = success(location = LOCATION, value = ORIGINAL_VALUE)

                        "then the method should return the original value" {
                            val alternative: JsReaderResult<String?> = result.ifNullValue { ALTERNATIVE_VALUE }

                            alternative shouldBeSameInstanceAs result
                        }
                    }

                    "when the value is null" - {
                        val result: JsReaderResult<String?> = success(location = LOCATION, value = null)

                        "then the method should return the default value" {
                            val alternative: JsReaderResult<String?> = result.ifNullValue { ALTERNATIVE_VALUE }

                            alternative shouldBeSuccess success(location = LOCATION, value = ALTERNATIVE_VALUE)
                        }
                    }
                }

                "when result is failure" - {
                    val result: JsReaderResult<String> =
                        failure(location = LOCATION, error = JsonErrors.PathMissing)

                    "then the method should return the original value" {
                        val alternative: JsReaderResult<String> = result.ifNullValue { ALTERNATIVE_VALUE }

                        alternative shouldBeSameInstanceAs result
                    }
                }
            }
        }

        "the extension function `toSuccess`" {
            val result = ORIGINAL_VALUE.toSuccess(LOCATION)

            result shouldBeSuccess JsReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)
        }

        "the extension function `E#toFailure`" {
            val result = JsonErrors.PathMissing.toFailure(LOCATION)

            result shouldBeFailure JsReaderResult.Failure(
                location = LOCATION,
                error = JsonErrors.PathMissing
            )
        }
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): JsReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}
