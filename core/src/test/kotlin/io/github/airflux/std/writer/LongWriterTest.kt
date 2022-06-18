package io.github.airflux.std.writer

import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class LongWriterTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The long type writer" - {
            val value: Long = Long.MAX_VALUE

            "should return JsNumber" {
                val result = LongWriter.write(CONTEXT, LOCATION, value)
                result shouldBe JsNumber.valueOf(value)
            }
        }
    }
}
