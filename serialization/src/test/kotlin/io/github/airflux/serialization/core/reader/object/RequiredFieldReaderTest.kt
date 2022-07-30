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

package io.github.airflux.serialization.core.reader.`object`

import io.github.airflux.serialization.common.DummyReader
import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.JsLookup
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class RequiredFieldReaderTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(PathMissingErrorBuilder(builder = { JsonErrors.PathMissing }))
        private val LOCATION = JsLocation.empty.append("name")
        private const val VALUE = "user-1"
        private val READER: JsReader<String> =
            DummyReader { _, location -> JsResult.Success(location = location, value = VALUE) }
    }

    init {

        "The readRequired function" - {

            "when the element is defined" - {
                val from: JsLookup = JsLookup.Defined(location = LOCATION, value = StringNode(VALUE))

                "then should return the result of applying the reader" {
                    val result: JsResult<String?> = readRequired(context = CONTEXT, from = from, using = READER)
                    result shouldBe JsResult.Success(location = LOCATION, value = VALUE)
                }
            }

            "when the element is undefined" - {
                val from: JsLookup = JsLookup.Undefined(location = LOCATION)

                "then should return the missing path error" {
                    val result: JsResult<String?> = readRequired(context = CONTEXT, from = from, using = READER)
                    result shouldBe JsResult.Failure(location = LOCATION, error = JsonErrors.PathMissing)
                }
            }
        }
    }
}
