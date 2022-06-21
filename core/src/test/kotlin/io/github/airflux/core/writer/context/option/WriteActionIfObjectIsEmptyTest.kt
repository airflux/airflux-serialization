package io.github.airflux.core.writer.context.option

import io.github.airflux.core.writer.context.JsWriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class WriteActionIfObjectIsEmptyTest : FreeSpec() {

    init {
        "The extension-function writeActionIfObjectIsEmpty" - {

            "when the writer context does not contain the WriteActionIfObjectIsEmpty option" - {
                val context: JsWriterContext = JsWriterContext()

                "should return the default value" {
                    val result = context.writeActionIfObjectIsEmpty
                    result shouldBe WriteActionIfObjectIsEmpty.Action.SKIP
                }
            }

            "when the writer context contain the WriteActionIfObjectIsEmpty option" - {
                val context = JsWriterContext(
                    WriteActionIfObjectIsEmpty(WriteActionIfObjectIsEmpty.Action.NULL)
                )

                "should return the option value" {
                    val result = context.writeActionIfObjectIsEmpty
                    result shouldBe WriteActionIfObjectIsEmpty.Action.NULL
                }
            }
        }
    }
}
