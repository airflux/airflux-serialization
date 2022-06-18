package io.github.airflux.std.writer

import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ByteWriterTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The byte type writer" - {
            val value: Byte = Byte.MAX_VALUE

            "should return JsNumber" {
                val result = ByteWriter.write(CONTEXT, LOCATION, value)
                result shouldBe JsNumber.valueOf(value)
            }
        }
    }
}
