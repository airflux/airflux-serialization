package io.github.airflux.std.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec

internal class StringReaderTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext(
            InvalidTypeErrorBuilder(JsonErrors::InvalidType)
        )
        private const val TEXT = "abc"
    }

    init {
        "The string type reader" - {

            "should return the string value" {
                val input: JsValue = JsString(TEXT)
                val result = StringReader.read(context, JsLocation.empty, input)
                result.assertAsSuccess(location = JsLocation.empty, value = TEXT)
            }

            "should return the invalid type error" {
                val input: JsValue = JsBoolean.valueOf(true)
                val result = StringReader.read(context, JsLocation.empty, input)
                result.assertAsFailure(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty,
                        error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                    )
                )
            }
        }
    }
}
