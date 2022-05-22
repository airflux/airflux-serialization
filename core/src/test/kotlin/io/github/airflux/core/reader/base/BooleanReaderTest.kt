package io.github.airflux.core.reader.base

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec

class BooleanReaderTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
    }

    init {

        "The boolean type reader" - {

            "should return value the true" {
                val input: JsValue = JsBoolean.valueOf(true)
                val result = BooleanReader.read(context, JsLocation.empty, input)
                result.assertAsSuccess(location = JsLocation.empty, value = true)
            }

            "should return value the false" {
                val input: JsValue = JsBoolean.valueOf(false)
                val result = BooleanReader.read(context, JsLocation.empty, input)
                result.assertAsSuccess(location = JsLocation.empty, value = false)
            }

            "should return the invalid type error" {
                val input: JsValue = JsString("abc")
                val result = BooleanReader.read(context, JsLocation.empty, input)
                result.assertAsFailure(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty,
                        error = JsonErrors.InvalidType(expected = JsValue.Type.BOOLEAN, actual = JsValue.Type.STRING)
                    )
                )
            }
        }
    }
}
