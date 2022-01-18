package io.github.airflux.core.value.extension

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsNumberTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation.empty.append("user")
        private val reader = { location: JsLocation, text: String -> JsResult.Success(text.toInt(), location) }
    }

    init {
        "The 'readAsNumber' function" - {
            "when called with a receiver of a 'JsNumber'" - {
                "should return the number value" {
                    val json: JsValue = JsNumber.valueOf(Int.MAX_VALUE)
                    val result = json.readAsNumber(LOCATION, JsonErrors::InvalidType, reader)
                    result.assertAsSuccess(location = LOCATION, value = Int.MAX_VALUE)
                }
            }
            "when called with a receiver of a not 'JsNumber'" - {
                "should return the 'InvalidType' error" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsNumber(LOCATION, JsonErrors::InvalidType, reader)
                    result.assertAsFailure(
                        LOCATION bind JsonErrors.InvalidType(
                            expected = JsValue.Type.NUMBER,
                            actual = JsValue.Type.BOOLEAN
                        )
                    )
                }
            }
        }
    }
}
