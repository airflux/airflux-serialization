package io.github.airflux.core.reader.context.option

import io.github.airflux.core.reader.context.JsReaderContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

internal class FailFastTest : FreeSpec() {

    init {
        "The extension-function failFast" - {

            "when the writer context does not contain the FailFast option" - {
                val context: JsReaderContext = JsReaderContext()

                "should return the default value" {
                    val result = context.failFast
                    result.shouldBeTrue()
                }
            }

            "when the writer context contain the FailFast option" - {
                val context = JsReaderContext(FailFast(false))

                "should return the option value" {
                    val result = context.failFast
                    result.shouldBeFalse()
                }
            }
        }
    }
}
