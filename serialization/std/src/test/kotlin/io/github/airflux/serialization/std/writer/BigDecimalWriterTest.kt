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

package io.github.airflux.serialization.std.writer

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsNumber
import io.github.airflux.serialization.core.writer.env.JsWriterEnv
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class BigDecimalWriterTest : FreeSpec() {

    companion object {
        private val ENV = JsWriterEnv(config = JsWriterEnv.Config(options = Unit))
        private val LOCATION: JsLocation = JsLocation
        private const val TEXT_VALUE = "10.50"
        private const val TEXT_VALUE_WITHOUT_TRAILING_ZEROS = "10.5"
    }

    init {

        "The big decimal type writer" - {

            "when the stripTrailingZeros option is true" - {
                val writer = BigDecimalWriter.build<Unit>(stripTrailingZeros = true)

                "then the writer should return the value without trailing zeros" {
                    val result = writer.write(ENV, LOCATION, BigDecimal(TEXT_VALUE))
                    result shouldBe JsNumber.valueOf(TEXT_VALUE_WITHOUT_TRAILING_ZEROS)!!
                }
            }

            "when the stripTrailingZeros option is false" - {
                val writer = BigDecimalWriter.build<Unit>(stripTrailingZeros = false)

                "then the writer should return the original value" {
                    val result = writer.write(ENV, LOCATION, BigDecimal(TEXT_VALUE))
                    result shouldBe JsNumber.valueOf(TEXT_VALUE)!!
                }
            }
        }
    }
}
