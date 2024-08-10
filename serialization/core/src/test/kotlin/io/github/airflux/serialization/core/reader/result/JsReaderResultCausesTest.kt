/*
 * Copyright 2021-2024 Maxim Sambulat.
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
import io.github.airflux.serialization.core.reader.result.JsReaderResult.Failure.Cause
import io.github.airflux.serialization.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly

internal class JsReaderResultCausesTest : FreeSpec() {

    init {

        "The `Causes` type" - {

            "when only one item is passed to create an instance of the type" - {
                val cause = Cause(location = LOCATION, error = FIRST_ERROR)
                val causes = JsReaderResult.Failure.Causes(cause)

                "then the new instance of the type should only contain the passed element" {
                    causes.items shouldContainExactly listOf(cause)
                }
            }

            "when another instance of the type is added to the instance of the type" - {
                val firstCause = Cause(location = LOCATION, error = FIRST_ERROR)
                val secondCause = Cause(location = LOCATION, error = SECOND_ERROR)
                val causes = JsReaderResult.Failure.Causes(firstCause) +
                    JsReaderResult.Failure.Causes(secondCause)

                "then the new instance of the type should contain elements from both instances in the order in which they were in the originals" {
                    causes.items shouldContainExactly listOf(firstCause, secondCause)
                }
            }
        }
    }

    internal companion object {
        private val LOCATION: JsLocation = JsLocation
        private val FIRST_ERROR = JsonErrors.InvalidType(JsValue.Type.STRING, JsValue.Type.NUMBER)
        private val SECOND_ERROR = JsonErrors.PathMissing
    }
}
