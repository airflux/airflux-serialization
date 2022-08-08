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

package io.github.airflux.serialization.core.reader.array

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.context.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.FailFast
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.github.airflux.serialization.std.reader.IntReader
import io.github.airflux.serialization.std.reader.LongReader
import io.github.airflux.serialization.std.reader.StringReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ArrayPropertyReaderTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE = "123"
        private const val SECOND_PHONE_VALUE = "456"

        private val CONTEXT = ReaderContext(
            listOf(
                InvalidTypeErrorBuilder(JsonErrors::InvalidType),
                AdditionalItemsErrorBuilder { JsonErrors.AdditionalItems }
            )
        )
        private val LOCATION = Location.empty
    }

    init {

        "The readArray function for the items-only reader" - {

            "when parameter 'source' is empty" - {
                val source = ArrayNode<StringNode>()

                val result: ReaderResult<List<String>> =
                    readArray(context = CONTEXT, location = LOCATION, source = source, items = StringReader)

                result shouldBe ReaderResult.Success(location = LOCATION, value = emptyList())
            }

            "when parameter 'source' is not empty" - {

                "when read was any errors" {
                    val source = ArrayNode(StringNode(FIRST_PHONE_VALUE), StringNode(SECOND_PHONE_VALUE))

                    val result: ReaderResult<List<String>> =
                        readArray(context = CONTEXT, location = LOCATION, source = source, items = StringReader)

                    result shouldBe ReaderResult.Success(
                        location = LOCATION,
                        value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                    )
                }

                "when read was some errors" - {
                    val source = ArrayNode(NumberNode.valueOf(10), BooleanNode.True)

                    "when fail-fast is true" {
                        val updatedContext: ReaderContext = CONTEXT + FailFast(true)

                        val result: ReaderResult<List<String>> = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            source = source,
                            items = StringReader
                        )

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.NUMBER
                                )
                            )
                        )
                    }

                    "when fail-fast is false" {
                        val updatedContext: ReaderContext = CONTEXT + FailFast(false)

                        val result: ReaderResult<List<String>> = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            source = source,
                            items = StringReader
                        )

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.NUMBER
                                )
                            ),
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(1),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.STRING,
                                    actual = ValueNode.Type.BOOLEAN
                                )
                            )
                        )
                    }
                }
            }
        }

        "The readArray function for the prefix-items-only readers" - {

            "when parameter 'source' is empty" - {
                val source = ArrayNode<StringNode>()

                val result: ReaderResult<List<String>> = readArray(
                    context = CONTEXT,
                    location = LOCATION,
                    source = source,
                    prefixItems = listOf(StringReader),
                    errorIfAdditionalItems = true
                )

                result shouldBe ReaderResult.Success(location = LOCATION, value = emptyList())
            }

            "when parameter 'source' is not empty" - {
                val source = ArrayNode(StringNode(FIRST_PHONE_VALUE), StringNode(SECOND_PHONE_VALUE))

                "when the number of readers is less than the number of items" - {
                    val readers = listOf(StringReader)

                    "when errorIfAdditionalItems is true" {
                        val result: ReaderResult<List<String>> = readArray(
                            context = CONTEXT,
                            location = LOCATION,
                            source = source,
                            prefixItems = readers,
                            errorIfAdditionalItems = true
                        )

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(1),
                                error = JsonErrors.AdditionalItems
                            )
                        )
                    }

                    "when errorIfAdditionalItems is false" {
                        val result: ReaderResult<List<String>> = readArray(
                            context = CONTEXT,
                            location = LOCATION,
                            source = source,
                            prefixItems = readers,
                            errorIfAdditionalItems = false
                        )

                        result shouldBe ReaderResult.Success(
                            location = LOCATION,
                            value = listOf(FIRST_PHONE_VALUE)
                        )
                    }
                }

                "when the number of readers is equal to the number of items" {
                    val result: ReaderResult<List<String>> = readArray(
                        context = CONTEXT,
                        location = LOCATION,
                        source = source,
                        prefixItems = listOf(StringReader, StringReader),
                        errorIfAdditionalItems = true
                    )

                    result shouldBe ReaderResult.Success(
                        location = LOCATION,
                        value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                    )
                }

                "when the number of readers is more than the number of items" {
                    val result: ReaderResult<List<String>> = readArray(
                        context = CONTEXT,
                        location = LOCATION,
                        source = source,
                        prefixItems = listOf(StringReader, StringReader, StringReader),
                        errorIfAdditionalItems = true
                    )

                    result shouldBe ReaderResult.Success(
                        location = LOCATION,
                        value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                    )
                }

                "when read was some errors" - {

                    "when fail-fast is true" {
                        val updatedContext: ReaderContext = CONTEXT + FailFast(true)

                        val result: ReaderResult<List<Number>> = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            source = source,
                            prefixItems = listOf(IntReader, LongReader),
                            errorIfAdditionalItems = true
                        )

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.NUMBER,
                                    actual = ValueNode.Type.STRING
                                )
                            )
                        )
                    }

                    "when fail-fast is false" {
                        val updatedContext: ReaderContext = CONTEXT + FailFast(false)

                        val result = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            source = source,
                            prefixItems = listOf(IntReader, io.github.airflux.serialization.std.reader.BooleanReader),
                            errorIfAdditionalItems = true
                        )

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.NUMBER,
                                    actual = ValueNode.Type.STRING
                                )
                            ),
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(1),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.BOOLEAN,
                                    actual = ValueNode.Type.STRING
                                )
                            )
                        )
                    }
                }
            }
        }

        "The readArray function for the prefix-items and items readers" - {

            "when parameter 'source' is empty" - {
                val source = ArrayNode<StringNode>()

                val result: ReaderResult<List<String>> = readArray(
                    context = CONTEXT,
                    location = LOCATION,
                    source = source,
                    prefixItems = listOf(StringReader),
                    items = StringReader
                )

                result shouldBe ReaderResult.Success(location = LOCATION, value = emptyList())
            }

            "when parameter 'source' is not empty" - {
                val source = ArrayNode(StringNode(FIRST_PHONE_VALUE), StringNode(SECOND_PHONE_VALUE))

                "when read was any errors" {
                    val result: ReaderResult<List<String>> = readArray(
                        context = CONTEXT,
                        location = LOCATION,
                        source = source,
                        prefixItems = listOf(StringReader),
                        items = StringReader
                    )

                    result shouldBe ReaderResult.Success(
                        location = LOCATION,
                        value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                    )
                }

                "when read was some errors" - {

                    "when fail-fast is true" {
                        val updatedContext: ReaderContext = CONTEXT + FailFast(true)

                        val result: ReaderResult<List<Int>> = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            source = source,
                            prefixItems = listOf(IntReader),
                            items = IntReader
                        )

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.NUMBER,
                                    actual = ValueNode.Type.STRING
                                )
                            )
                        )
                    }

                    "when fail-fast is false" {
                        val updatedContext: ReaderContext = CONTEXT + FailFast(false)

                        val result = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            source = source,
                            prefixItems = listOf(IntReader),
                            items = IntReader
                        )

                        result as ReaderResult.Failure
                        result.causes shouldContainExactly listOf(
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.NUMBER,
                                    actual = ValueNode.Type.STRING
                                )
                            ),
                            ReaderResult.Failure.Cause(
                                location = LOCATION.append(1),
                                error = JsonErrors.InvalidType(
                                    expected = ValueNode.Type.NUMBER,
                                    actual = ValueNode.Type.STRING
                                )
                            )
                        )
                    }
                }
            }
        }

        "The extension-function ReaderResult<MutableList<T>>#plus(ReaderResult.Success<T>)" - {
            val parameter: ReaderResult.Success<String> = ReaderResult.Success(LOCATION.append(1), SECOND_PHONE_VALUE)

            "when receiver is success" {
                val receiver: ReaderResult<MutableList<String>> =
                    mutableListOf(FIRST_PHONE_VALUE).success(LOCATION.append(0))

                val result = receiver + parameter

                result shouldBe ReaderResult.Success(
                    location = LOCATION.append(0),
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
                val receiver: ReaderResult<MutableList<String>> =
                    mutableListOf(FIRST_PHONE_VALUE).success(LOCATION.append(0))

                val result = receiver + parameter

                result shouldBe parameter
            }

            "when receiver is failure" {
                val receiver: ReaderResult<MutableList<String>> =
                    JsonErrors.InvalidType(expected = ValueNode.Type.NUMBER, actual = ValueNode.Type.BOOLEAN)
                        .failure(LOCATION.append(0))

                val result = receiver + parameter

                result as ReaderResult.Failure
                result.causes shouldContainExactly listOf(
                    ReaderResult.Failure.Cause(
                        location = LOCATION.append(0),
                        error = JsonErrors.InvalidType(
                            expected = ValueNode.Type.NUMBER,
                            actual = ValueNode.Type.BOOLEAN
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
}
