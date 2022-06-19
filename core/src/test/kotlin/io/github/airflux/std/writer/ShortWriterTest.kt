package io.github.airflux.std.writer

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ShortWriterTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The short type writer" - {
            val value: Short = Short.MAX_VALUE

            "should return JsNumber" {
                val result = ShortWriter.write(CONTEXT, LOCATION, value)
                result shouldBe JsNumber.valueOf(value)
            }
        }
    }
}
