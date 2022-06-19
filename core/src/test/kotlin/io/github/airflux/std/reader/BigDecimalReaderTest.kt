package io.github.airflux.std.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import java.math.BigDecimal

internal class BigDecimalReaderTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
    }

    init {

        "The big decimal type reader" - {

            "should return the big decimal value" - {
                withData(
                    listOf("-10.5", "-10", "-0.5", "0", "0.5", "10", "10.5")
                ) { value ->
                    val input: JsValue = JsNumber.valueOf(value)!!
                    val result = BigDecimalReader.read(context, JsLocation.empty, input)
                    result.assertAsSuccess(location = JsLocation.empty, value = BigDecimal(value))
                }
            }

            "should return the invalid type error" {
                val input: JsValue = JsString("abc")
                val result = BigDecimalReader.read(context, JsLocation.empty, input)
                result.assertAsFailure(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty,
                        error = JsonErrors.InvalidType(expected = JsValue.Type.NUMBER, actual = JsValue.Type.STRING)
                    )
                )
            }
        }
    }
}
