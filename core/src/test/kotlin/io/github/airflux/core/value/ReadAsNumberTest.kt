package io.github.airflux.core.value

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsNumberTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private val LOCATION = JsLocation.empty.append("user")
        private val reader =
            { _: JsReaderContext, location: JsLocation, text: String -> JsResult.Success(location, text.toInt()) }
    }

    init {
        "The 'readAsNumber' function" - {
            "when called with a receiver of a 'JsNumber'" - {
                "should return the number value" {
                    val json: JsValue = JsNumber.valueOf(Int.MAX_VALUE)
                    val result = json.readAsNumber(CONTEXT, LOCATION, reader)
                    result.assertAsSuccess(location = LOCATION, value = Int.MAX_VALUE)
                }
            }
            "when called with a receiver of a not 'JsNumber'" - {
                "should return the 'InvalidType' error" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsNumber(CONTEXT, LOCATION, reader)
                    result.assertAsFailure(
                        JsResult.Failure.Cause(
                            location = LOCATION,
                            error = JsonErrors.InvalidType(
                                expected = JsValue.Type.NUMBER,
                                actual = JsValue.Type.BOOLEAN
                            )
                        )
                    )
                }
            }
        }
    }
}
