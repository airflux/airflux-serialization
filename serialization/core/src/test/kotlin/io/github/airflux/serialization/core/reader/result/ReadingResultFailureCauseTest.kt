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
import io.github.airflux.serialization.test.kotest.shouldBeEqualsContract
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ReadingResultFailureCauseTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation
    }

    init {
        "A ReadingResult#Failure#Cause type" - {

            "constructor(JsLocation, Error)" {
                val cause = ReadingResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing)

                cause.location shouldBe LOCATION
                cause.errors shouldBe ReadingResult.Errors(JsonErrors.PathMissing)
            }

            "constructor(JsLocation, ReadingResult#Errors)" {
                val cause = ReadingResult.Failure.Cause(
                    location = LOCATION,
                    errors = ReadingResult.Errors(JsonErrors.PathMissing)
                )

                cause.location shouldBe LOCATION
                cause.errors.items shouldContainExactly listOf(JsonErrors.PathMissing)
            }

            "should comply with equals() and hashCode() contract" {
                ReadingResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing).shouldBeEqualsContract(
                    y = ReadingResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing),
                    z = ReadingResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing),
                    others = listOf(
                        ReadingResult.Failure.Cause(location = LOCATION, error = JsonErrors.AdditionalItems),
                        ReadingResult.Failure.Cause(location = LOCATION.append("id"), error = JsonErrors.PathMissing)
                    )
                )
            }
        }
    }
}
