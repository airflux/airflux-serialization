package io.github.airflux.core.writer.context.option

import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class WriteActionIfArrayIsEmptyTest : FreeSpec() {

    init {
        "The extension-function writeActionIfArrayIsEmpty" - {

            "when the writer context does not contain the WriteActionIfArrayIsEmpty option" - {
                val context = JsWriterContext()

                "should return the default value" {
                    val result = context.writeActionIfArrayIsEmpty
                    result shouldBe WriteActionIfArrayIsEmpty.Action.SKIP
                }
            }

            "when the writer context contain the WriteActionIfArrayIsEmpty option" - {
                val context = JsWriterContext(
                    WriteActionIfArrayIsEmpty(WriteActionIfArrayIsEmpty.Action.NULL)
                )

                "should return the option value" {
                    val result = context.writeActionIfArrayIsEmpty
                    result shouldBe WriteActionIfArrayIsEmpty.Action.NULL
                }
            }
        }
    }
}
