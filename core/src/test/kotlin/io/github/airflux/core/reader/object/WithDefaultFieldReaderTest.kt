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

package io.github.airflux.core.reader.`object`

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class WithDefaultFieldReaderTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private const val DEFAULT_VALUE = "Default value"
        private val stringReader: JsReader<String> = JsReader { _, location, input ->
            JsResult.Success(location, (input as JsString).get)
        }
        private val defaultValue = { DEFAULT_VALUE }
    }

    init {

        "The 'readWithDefault' function" - {

            "should return the result of applying the reader to the node if it is not the JsNull node" {
                val from: JsLookup =
                    JsLookup.Defined(location = JsLocation.empty.append("name"), JsString(USER_NAME_VALUE))

                val result: JsResult<String> =
                    readWithDefault(context = CONTEXT, from = from, using = stringReader, defaultValue = defaultValue)

                result shouldBe JsResult.Success(location = JsLocation.empty.append("name"), value = USER_NAME_VALUE)
            }

            "should return the default value if found a JsNull node" {
                val from: JsLookup = JsLookup.Defined(location = JsLocation.empty.append("name"), JsNull)

                val result: JsResult<String> =
                    readWithDefault(context = CONTEXT, from = from, using = stringReader, defaultValue = defaultValue)

                result shouldBe JsResult.Success(location = JsLocation.empty.append("name"), value = DEFAULT_VALUE)
            }

            "should return the default value if did not find a node" {
                val from: JsLookup = JsLookup.Undefined.PathMissing(location = JsLocation.empty.append("name"))

                val result: JsResult<String> =
                    readWithDefault(context = CONTEXT, from = from, using = stringReader, defaultValue = defaultValue)

                result shouldBe JsResult.Success(location = JsLocation.empty.append("name"), value = DEFAULT_VALUE)
            }

            "should return the invalid type error if a node is not an object" {
                val from: JsLookup = JsLookup.Undefined.InvalidType(
                    location = JsLocation.empty.append("name"),
                    expected = JsValue.Type.OBJECT,
                    actual = JsValue.Type.STRING
                )

                val result: JsResult<String> =
                    readWithDefault(context = CONTEXT, from = from, using = stringReader, defaultValue = defaultValue)

                result shouldBe JsResult.Failure(
                    location = JsLocation.empty.append("name"),
                    error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
                )
            }
        }
    }
}
