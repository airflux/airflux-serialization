package io.github.airflux.value.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsBooleanTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation.Root / "user"
    }

    init {
        "The 'readAsBoolean' function" - {
            "when called with a receiver of a 'JsBoolean'" - {
                "should return the boolean value" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsBoolean(LOCATION, JsonErrors::InvalidType)
                    result.assertAsSuccess(location = LOCATION, value = true)
                }
            }
            "when called with a receiver of a not 'JsBoolean'" - {
                "should return the 'InvalidType' error" {
                    val json = JsString("abc")
                    val result = json.readAsBoolean(LOCATION, JsonErrors::InvalidType)
                    result.assertAsFailure(
                        LOCATION bind JsonErrors.InvalidType(
                            expected = JsValue.Type.BOOLEAN,
                            actual = JsValue.Type.STRING
                        )
                    )
                }
            }
        }
    }
}
