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
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.context.option.FailFast
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsBoolean
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.std.reader.IntReader
import io.github.airflux.serialization.std.reader.LongReader
import io.github.airflux.serialization.std.reader.StringReader
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ArrayFieldReaderTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE = "123"
        private const val SECOND_PHONE_VALUE = "456"

        private val CONTEXT = JsReaderContext(
            listOf(
                InvalidTypeErrorBuilder(JsonErrors::InvalidType),
                AdditionalItemsErrorBuilder { JsonErrors.AdditionalItems }
            )
        )
        private val LOCATION = JsLocation.empty
    }

    init {

        "The readArray function for the items-only reader" - {

            "when parameter 'from' is empty" - {
                val from = JsArray<JsString>()

                val result: JsResult<List<String>> =
                    readArray(context = CONTEXT, location = LOCATION, from = from, items = StringReader)

                result shouldBe JsResult.Success(location = LOCATION, value = emptyList())
            }

            "when parameter 'from' is not empty" - {

                "when read was any errors" {
                    val from = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

                    val result: JsResult<List<String>> =
                        readArray(context = CONTEXT, location = LOCATION, from = from, items = StringReader)

                    result shouldBe JsResult.Success(
                        location = LOCATION,
                        value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                    )
                }

                "when read was some errors" - {
                    val from = JsArray(JsNumber.valueOf(10), JsBoolean.True)

                    "when fail-fast is true" {
                        val updatedContext: JsReaderContext = CONTEXT + FailFast(true)

                        val result: JsResult<List<String>> =
                            readArray(context = updatedContext, location = LOCATION, from = from, items = StringReader)

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            )
                        )
                    }

                    "when fail-fast is false" {
                        val updatedContext: JsReaderContext = CONTEXT + FailFast(false)

                        val result: JsResult<List<String>> =
                            readArray(context = updatedContext, location = LOCATION, from = from, items = StringReader)

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.NUMBER
                                )
                            ),
                            JsResult.Failure.Cause(
                                location = LOCATION.append(1),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.STRING,
                                    actual = JsValue.Type.BOOLEAN
                                )
                            )
                        )
                    }
                }
            }
        }

        "The readArray function for the prefix-items-only readers" - {

            "when parameter 'from' is empty" - {
                val from = JsArray<JsString>()

                val result: JsResult<List<String>> = readArray(
                    context = CONTEXT,
                    location = LOCATION,
                    from = from,
                    prefixItems = listOf(StringReader),
                    errorIfAdditionalItems = true
                )

                result shouldBe JsResult.Success(location = LOCATION, value = emptyList())
            }

            "when parameter 'from' is not empty" - {
                val from = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

                "when the number of readers is less than the number of items" - {
                    val readers = listOf(StringReader)

                    "when errorIfAdditionalItems is true" {
                        val result: JsResult<List<String>> = readArray(
                            context = CONTEXT,
                            location = LOCATION,
                            from = from,
                            prefixItems = readers,
                            errorIfAdditionalItems = true
                        )

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append(1),
                                error = JsonErrors.AdditionalItems
                            )
                        )
                    }

                    "when errorIfAdditionalItems is false" {
                        val result: JsResult<List<String>> = readArray(
                            context = CONTEXT,
                            location = LOCATION,
                            from = from,
                            prefixItems = readers,
                            errorIfAdditionalItems = false
                        )

                        result shouldBe JsResult.Success(
                            location = LOCATION,
                            value = listOf(FIRST_PHONE_VALUE)
                        )
                    }
                }

                "when the number of readers is equal to the number of items" {
                    val result: JsResult<List<String>> = readArray(
                        context = CONTEXT,
                        location = LOCATION,
                        from = from,
                        prefixItems = listOf(StringReader, StringReader),
                        errorIfAdditionalItems = true
                    )

                    result shouldBe JsResult.Success(
                        location = LOCATION,
                        value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                    )
                }

                "when the number of readers is more than the number of items" {
                    val result: JsResult<List<String>> = readArray(
                        context = CONTEXT,
                        location = LOCATION,
                        from = from,
                        prefixItems = listOf(StringReader, StringReader, StringReader),
                        errorIfAdditionalItems = true
                    )

                    result shouldBe JsResult.Success(
                        location = LOCATION,
                        value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                    )
                }

                "when read was some errors" - {

                    "when fail-fast is true" {
                        val updatedContext: JsReaderContext = CONTEXT + FailFast(true)

                        val result: JsResult<List<Number>> = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            from = from,
                            prefixItems = listOf(IntReader, LongReader),
                            errorIfAdditionalItems = true
                        )

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        )
                    }

                    "when fail-fast is false" {
                        val updatedContext: JsReaderContext = CONTEXT + FailFast(false)

                        val result = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            from = from,
                            prefixItems = listOf(IntReader, io.github.airflux.serialization.std.reader.BooleanReader),
                            errorIfAdditionalItems = true
                        )

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.STRING
                                )
                            ),
                            JsResult.Failure.Cause(
                                location = LOCATION.append(1),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.BOOLEAN,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        )
                    }
                }
            }
        }

        "The readArray function for the prefix-items and items readers" - {

            "when parameter 'from' is empty" - {
                val from = JsArray<JsString>()

                val result: JsResult<List<String>> = readArray(
                    context = CONTEXT,
                    location = LOCATION,
                    from = from,
                    prefixItems = listOf(StringReader),
                    items = StringReader
                )

                result shouldBe JsResult.Success(location = LOCATION, value = emptyList())
            }

            "when parameter 'from' is not empty" - {
                val from = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

                "when read was any errors" {
                    val result: JsResult<List<String>> = readArray(
                        context = CONTEXT,
                        location = LOCATION,
                        from = from,
                        prefixItems = listOf(StringReader),
                        items = StringReader
                    )

                    result shouldBe JsResult.Success(
                        location = LOCATION,
                        value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                    )
                }

                "when read was some errors" - {

                    "when fail-fast is true" {
                        val updatedContext: JsReaderContext = CONTEXT + FailFast(true)

                        val result: JsResult<List<Int>> = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            from = from,
                            prefixItems = listOf(IntReader),
                            items = IntReader
                        )

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        )
                    }

                    "when fail-fast is false" {
                        val updatedContext: JsReaderContext = CONTEXT + FailFast(false)

                        val result = readArray(
                            context = updatedContext,
                            location = LOCATION,
                            from = from,
                            prefixItems = listOf(IntReader),
                            items = IntReader
                        )

                        result as JsResult.Failure
                        result.causes shouldContainExactly listOf(
                            JsResult.Failure.Cause(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.STRING
                                )
                            ),
                            JsResult.Failure.Cause(
                                location = LOCATION.append(1),
                                error = JsonErrors.InvalidType(
                                    expected = JsValue.Type.NUMBER,
                                    actual = JsValue.Type.STRING
                                )
                            )
                        )
                    }
                }
            }
        }

        "The extension-function JsResult<MutableList<T>>#plus(JsResult.Success<T>)" - {
            val parameter: JsResult.Success<String> = JsResult.Success(LOCATION.append(1), SECOND_PHONE_VALUE)

            "when receiver is success" {
                val receiver: JsResult<MutableList<String>> =
                    mutableListOf(FIRST_PHONE_VALUE).success(LOCATION.append(0))

                val result = receiver + parameter

                result shouldBe JsResult.Success(
                    location = LOCATION.append(0),
                    value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                )
            }

            "when receiver is failure" {
                val receiver: JsResult<MutableList<String>> = JsonErrors.PathMissing.failure(LOCATION.append(0))

                val result = receiver + parameter

                result shouldBe receiver
            }
        }

        "The extension-function JsResult<MutableList<T>>#plus(JsResult.Failure)" - {
            val parameter: JsResult.Failure = JsResult.Failure(LOCATION.append(1), JsonErrors.PathMissing)

            "when receiver is success" {
                val receiver: JsResult<MutableList<String>> =
                    mutableListOf(FIRST_PHONE_VALUE).success(LOCATION.append(0))

                val result = receiver + parameter

                result shouldBe parameter
            }

            "when receiver is failure" {
                val receiver: JsResult<MutableList<String>> =
                    JsonErrors.InvalidType(expected = JsValue.Type.NUMBER, actual = JsValue.Type.BOOLEAN)
                        .failure(LOCATION.append(0))

                val result = receiver + parameter

                result as JsResult.Failure
                result.causes shouldContainExactly listOf(
                    JsResult.Failure.Cause(
                        location = LOCATION.append(0),
                        error = JsonErrors.InvalidType(
                            expected = JsValue.Type.NUMBER,
                            actual = JsValue.Type.BOOLEAN
                        )
                    ),
                    JsResult.Failure.Cause(
                        location = LOCATION.append(1),
                        error = JsonErrors.PathMissing
                    )
                )
            }
        }
    }
}
