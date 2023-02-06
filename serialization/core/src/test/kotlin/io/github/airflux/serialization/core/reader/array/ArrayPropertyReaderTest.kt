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

package io.github.airflux.serialization.core.reader.array

import io.github.airflux.serialization.core.common.DummyReader
import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeFailure
import io.github.airflux.serialization.core.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.valueOf
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

internal class ArrayPropertyReaderTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE = "123"
        private const val SECOND_PHONE_VALUE = "456"
        private const val THIRD_PHONE_VALUE = "789"

        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val IntReader: Reader<EB, OPTS, Unit, Int> = DummyReader.int()
        private val LongReader: Reader<EB, OPTS, Unit, Long> = DummyReader.long()
        private val StringReader: Reader<EB, OPTS, Unit, String> = DummyReader.string()
        private val BooleanReader: Reader<EB, OPTS, Unit, Boolean> = DummyReader.boolean()
    }

    init {

        "The readArray function for the items-only reader" - {

            "when parameter 'source' is empty" - {
                val source = ArrayNode<StringNode>()

                "then reader should return result" - {
                    withData(
                        nameFn = { "when fail-fast is $it" },
                        listOf(true, false)
                    ) { failFast ->
                        val env = ReaderEnv(EB(), OPTS(failFast = failFast))
                        val result: ReaderResult<List<String>> = readArray(
                            env = env,
                            context = CONTEXT,
                            location = LOCATION,
                            source = source,
                            items = StringReader
                        )

                        result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = emptyList())
                    }
                }
            }

            "when parameter 'source' is not empty" - {

                "when read was no errors" - {
                    val source = ArrayNode(StringNode(FIRST_PHONE_VALUE), StringNode(SECOND_PHONE_VALUE))

                    "then reader should return result" - {
                        withData(
                            nameFn = { "when fail-fast is $it" },
                            listOf(true, false)
                        ) { failFast ->
                            val env = ReaderEnv(EB(), OPTS(failFast = failFast))
                            val result: ReaderResult<List<String>> = readArray(
                                env = env,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                items = StringReader
                            )

                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when read was some errors" - {
                    val source = ArrayNode(NumericNode.Integer.valueOf(10), BooleanNode.True)

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue =
                            ReaderEnv(errorBuilders = EB(), options = OPTS(failFast = true))

                        "then the validator should return first error" {
                            val result: ReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                items = StringReader
                            )

                            result shouldBeFailure ReaderResult.Failure(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(StringNode.nameOfType),
                                    actual = NumericNode.Integer.nameOfType
                                )
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                        "then the validator should return all errors" {
                            val result: ReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                items = StringReader
                            )

                            result.shouldBeFailure(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StringNode.nameOfType),
                                        actual = NumericNode.Integer.nameOfType
                                    )
                                ),
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(1),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(StringNode.nameOfType),
                                        actual = BooleanNode.nameOfType
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }

        "The readArray function for the prefix-items-only readers" - {

            "when parameter 'source' is empty" - {
                val source = ArrayNode<StringNode>()

                "then reader should return result" - {
                    withData(
                        nameFn = { "when fail-fast is $it" },
                        listOf(true, false)
                    ) { failFast ->
                        val env = ReaderEnv(EB(), OPTS(failFast = failFast))
                        val result: ReaderResult<List<String>> = readArray(
                            env = env,
                            context = CONTEXT,
                            location = LOCATION,
                            source = source,
                            prefixItems = listOf(StringReader),
                            errorIfAdditionalItems = true
                        )

                        result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = emptyList())
                    }
                }
            }

            "when parameter 'source' is not empty" - {
                val source = ArrayNode(
                    StringNode(FIRST_PHONE_VALUE),
                    StringNode(SECOND_PHONE_VALUE),
                    StringNode(THIRD_PHONE_VALUE)
                )

                "when the number of readers is less than the number of items" - {
                    val readers = listOf(StringReader)

                    "when errorIfAdditionalItems is true" - {
                        val errorIfAdditionalItems = true

                        "when fail-fast is true" - {
                            val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                            "then reader should return first error" {
                                val result: ReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsTrue,
                                    context = CONTEXT,
                                    location = LOCATION,
                                    source = source,
                                    prefixItems = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result shouldBeFailure ReaderResult.Failure(
                                    location = LOCATION.append(1),
                                    error = JsonErrors.AdditionalItems
                                )
                            }
                        }

                        "when fail-fast is false" - {
                            val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                            "then reader should return all errors" {
                                val result: ReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsFalse,
                                    context = CONTEXT,
                                    location = LOCATION,
                                    source = source,
                                    prefixItems = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result.shouldBeFailure(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(1),
                                        error = JsonErrors.AdditionalItems
                                    ),
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(2),
                                        error = JsonErrors.AdditionalItems
                                    )
                                )
                            }
                        }
                    }

                    "when errorIfAdditionalItems is false" - {
                        val errorIfAdditionalItems = false

                        "then reader should return result" - {
                            withData(
                                nameFn = { "when fail-fast is $it" },
                                listOf(true, false)
                            ) { failFast ->
                                val env = ReaderEnv(EB(), OPTS(failFast = failFast))
                                val result: ReaderResult<List<String>> = readArray(
                                    env = env,
                                    context = CONTEXT,
                                    location = LOCATION,
                                    source = source,
                                    prefixItems = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result shouldBeSuccess ReaderResult.Success(
                                    location = LOCATION,
                                    value = listOf(FIRST_PHONE_VALUE)
                                )
                            }
                        }
                    }
                }

                "when the number of readers is equal to the number of items" - {
                    val readers = listOf(StringReader, StringReader, StringReader)

                    "then reader should return result" - {
                        withData(
                            nameFn = { "when fail-fast is $it" },
                            listOf(true, false)
                        ) { failFast ->
                            val env = ReaderEnv(EB(), OPTS(failFast = failFast))
                            val result: ReaderResult<List<String>> = readArray(
                                env = env,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItems = readers,
                                errorIfAdditionalItems = true
                            )

                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when the number of readers is more than the number of items" - {
                    val readers = listOf(StringReader, StringReader, StringReader, StringReader)

                    "then reader should return result" - {
                        withData(
                            nameFn = { "when fail-fast is $it" },
                            listOf(true, false)
                        ) { failFast ->
                            val env = ReaderEnv(EB(), OPTS(failFast = failFast))
                            val result: ReaderResult<List<String>> = readArray(
                                env = env,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItems = readers,
                                errorIfAdditionalItems = true
                            )

                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when read was some errors" - {

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return first error" {
                            val result: ReaderResult<List<Number>> = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItems = listOf(IntReader, LongReader),
                                errorIfAdditionalItems = true
                            )

                            result shouldBeFailure ReaderResult.Failure(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(NumericNode.Integer.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return all errors" {
                            val result = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItems = listOf(IntReader, StringReader, BooleanReader),
                                errorIfAdditionalItems = true
                            )

                            result.shouldBeFailure(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumericNode.Integer.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                ),
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(2),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(BooleanNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }

        "The readArray function for the prefix-items and items readers" - {

            "when parameter 'source' is empty" - {
                val source = ArrayNode<StringNode>()

                "then reader should return result" - {
                    withData(
                        nameFn = { "when fail-fast is $it" },
                        listOf(true, false)
                    ) { failFast ->
                        val env = ReaderEnv(EB(), OPTS(failFast = failFast))
                        val result: ReaderResult<List<String>> = readArray(
                            env = env,
                            context = CONTEXT,
                            location = LOCATION,
                            source = source,
                            prefixItems = listOf(StringReader),
                            items = StringReader
                        )

                        result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = emptyList())
                    }
                }
            }

            "when parameter 'source' is not empty" - {
                val source = ArrayNode(StringNode(FIRST_PHONE_VALUE), StringNode(SECOND_PHONE_VALUE))

                "when read was no errors" - {

                    "then reader should return result" - {
                        withData(
                            nameFn = { "when fail-fast is $it" },
                            listOf(true, false)
                        ) { failFast ->
                            val env = ReaderEnv(EB(), OPTS(failFast = failFast))
                            val result: ReaderResult<List<String>> = readArray(
                                env = env,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItems = listOf(StringReader),
                                items = StringReader
                            )

                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when read was some errors" - {

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return first error" {
                            val result: ReaderResult<List<Int>> = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItems = listOf(IntReader),
                                items = IntReader
                            )

                            result shouldBeFailure ReaderResult.Failure(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(NumericNode.Integer.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return all errors" {
                            val result = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItems = listOf(IntReader),
                                items = IntReader
                            )

                            result.shouldBeFailure(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumericNode.Integer.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                ),
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(1),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumericNode.Integer.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }

        "The extension-function ReaderResult<MutableList<T>>#plus(ReaderResult.Success<T>)" - {
            val parameter: ReaderResult.Success<String> =
                ReaderResult.Success(location = LOCATION, value = SECOND_PHONE_VALUE)

            "when receiver is success" {
                val receiver: ReaderResult<MutableList<String>> =
                    mutableListOf(FIRST_PHONE_VALUE).success(LOCATION)

                val result = receiver + parameter

                result shouldBeSuccess ReaderResult.Success(
                    location = LOCATION,
                    value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                )
            }

            "when receiver is failure" {
                val receiver: ReaderResult<MutableList<String>> = JsonErrors.PathMissing.failure(LOCATION.append(0))

                val result = receiver + parameter

                result shouldBe receiver
            }
        }

        "The extension-function ReaderResult<MutableList<T>>#plus(ReaderResult.Failure)" - {
            val parameter: ReaderResult.Failure = ReaderResult.Failure(LOCATION.append(1), JsonErrors.PathMissing)

            "when receiver is success" {
                val receiver: ReaderResult<MutableList<String>> = mutableListOf(FIRST_PHONE_VALUE).success(LOCATION)

                val result = receiver + parameter

                result shouldBe parameter
            }

            "when receiver is failure" {
                val receiver: ReaderResult<MutableList<String>> =
                    JsonErrors.InvalidType(
                        expected = listOf(NumericNode.Integer.nameOfType),
                        actual = BooleanNode.nameOfType
                    ).failure(LOCATION.append(0))

                val result = receiver + parameter

                result.shouldBeFailure(
                    ReaderResult.Failure.Cause(
                        location = LOCATION.append(0),
                        error = JsonErrors.InvalidType(
                            expected = listOf(NumericNode.Integer.nameOfType),
                            actual = BooleanNode.nameOfType
                        )
                    ),
                    ReaderResult.Failure.Cause(
                        location = LOCATION.append(1),
                        error = JsonErrors.PathMissing
                    )
                )
            }
        }
    }

    internal class EB : AdditionalItemsErrorBuilder,
                        InvalidTypeErrorBuilder,
                        ValueCastErrorBuilder {
        override fun additionalItemsError(): ReaderResult.Error = JsonErrors.AdditionalItems

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)

        override fun valueCastError(value: String, target: KClass<*>): ReaderResult.Error =
            JsonErrors.ValueCast(value, target)
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
