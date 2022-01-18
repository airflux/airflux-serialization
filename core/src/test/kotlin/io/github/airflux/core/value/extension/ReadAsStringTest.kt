package io.github.airflux.core.value.extension

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsStringTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation.empty.append("user")
    }

    init {
        "The 'readAsString' function" - {
            "when called with a receiver of a 'JsString'" - {
                "should return the string value"{
                    val json: JsValue = JsString("abc")
                    val result = json.readAsString(LOCATION, JsonErrors::InvalidType)
                    result.assertAsSuccess(location = LOCATION, value = "abc")
                }
            }
            "when called with a receiver of a not 'JsString'" - {
                "should return the 'InvalidType' error"  {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsString(LOCATION, JsonErrors::InvalidType)
                    result.assertAsFailure(
                        LOCATION bind JsonErrors.InvalidType(
                            expected = JsValue.Type.STRING,
                            actual = JsValue.Type.BOOLEAN
                        )
                    )
                }
            }
        }
    }
}
