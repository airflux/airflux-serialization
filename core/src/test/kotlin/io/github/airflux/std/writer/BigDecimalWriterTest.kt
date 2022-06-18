package io.github.airflux.std.writer

import io.github.airflux.core.reader.result.JsLocation
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
