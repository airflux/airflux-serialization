package io.github.airflux.std.writer

import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class BooleanWriterTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The boolean type writer" - {

            "when the value contains the true value" - {
                val value = true

                "then writer should return JsBoolean.True" {
                    val result = BooleanWriter.write(CONTEXT, LOCATION, value)
                    result shouldBe JsBoolean.True
                }
            }

            "when the value contains the false value" - {
                val value = false

                "then writer should return JsBoolean.False" {
                    val result = BooleanWriter.write(CONTEXT, LOCATION, value)
                    result shouldBe JsBoolean.False
                }
            }
        }
    }
}
