package io.github.airflux.std.writer

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.value.JsString
import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class StringWriterTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The string type writer" - {
            val value = "value"

            "should return JsString" {
                val result = StringWriter.write(CONTEXT, LOCATION, value)
                result shouldBe JsString(value)
            }
        }
    }
}
