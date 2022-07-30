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

package io.github.airflux.serialization.core.reader.context.option

import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

internal class FailFastTest : FreeSpec() {

    init {
        "The extension-function failFast" - {

            "when the writer context does not contain the FailFast option" - {
                val context = ReaderContext()

                "should return the default value" {
                    val result = context.failFast
                    result.shouldBeTrue()
                }
            }

            "when the writer context contain the FailFast option" - {
                val context = ReaderContext(FailFast(false))

                "should return the option value" {
                    val result = context.failFast
                    result.shouldBeFalse()
                }
            }
        }
    }
}
