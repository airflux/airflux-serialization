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
import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.core.location.Location
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ReaderResultFailureCauseTest : FreeSpec() {

    companion object {
        private val LOCATION = Location.empty
    }

    init {
        "A ReaderResult#Failure#Cause type" - {

            "constructor(Location, Error)" {
                val cause = ReaderResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing)

                cause.location shouldBe LOCATION
                cause.errors shouldBe ReaderResult.Errors(JsonErrors.PathMissing)
            }

            "constructor(Location, ReaderResult#Errors)" {
                val cause = ReaderResult.Failure.Cause(
                    location = LOCATION,
                    errors = ReaderResult.Errors(JsonErrors.PathMissing)
                )

                cause.location shouldBe LOCATION
                cause.errors.items shouldContainExactly listOf(JsonErrors.PathMissing)
            }

            "should comply with equals() and hashCode() contract" {
                ReaderResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing).shouldBeEqualsContract(
                    y = ReaderResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing),
                    z = ReaderResult.Failure.Cause(location = LOCATION, error = JsonErrors.PathMissing),
                    others = listOf(
                        ReaderResult.Failure.Cause(location = LOCATION, error = JsonErrors.AdditionalItems),
                        ReaderResult.Failure.Cause(location = LOCATION.append("id"), error = JsonErrors.PathMissing)
                    )
                )
            }
        }
    }
}
