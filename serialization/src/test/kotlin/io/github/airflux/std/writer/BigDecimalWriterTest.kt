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

package io.github.airflux.std.writer

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class BigDecimalWriterTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation.empty
        private const val TEXT_VALUE = "10.50"
        private const val TEXT_VALUE_WITHOUT_TRAILING_ZEROS = "10.5"
    }

    init {

        "The big decimal type writer" - {

            "when the writer context does not contain the stripTrailingZeros option" - {
                val context = JsWriterContext()

                "then the writer should return original value" {
                    val result = BigDecimalWriter.write(context, LOCATION, BigDecimal(TEXT_VALUE))
                    result shouldBe JsNumber.valueOf(TEXT_VALUE)
                }
            }

            "when the writer context contain the stripTrailingZeros option" - {

                "when the stripTrailingZeros option is true" - {
                    val context = JsWriterContext(
                        BigDecimalWriter.StripTrailingZeros(true)
                    )

                    "then the writer should return the value without trailing zeros" {
                        val result = BigDecimalWriter.write(context, LOCATION, BigDecimal(TEXT_VALUE))
                        result shouldBe JsNumber.valueOf(TEXT_VALUE_WITHOUT_TRAILING_ZEROS)
                    }
                }

                "when the stripTrailingZeros option is false" - {
                    val context = JsWriterContext(
                        BigDecimalWriter.StripTrailingZeros(false)
                    )

                    "then the writer should return the original value" {
                        val result = BigDecimalWriter.write(context, LOCATION, BigDecimal(TEXT_VALUE))
                        result shouldBe JsNumber.valueOf(TEXT_VALUE)
                    }
                }
            }
        }
    }
}
