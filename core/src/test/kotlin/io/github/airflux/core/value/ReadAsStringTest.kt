package io.github.airflux.core.value

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsStringTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext(
            InvalidTypeErrorBuilder(JsonErrors::InvalidType)
        )
        private val LOCATION = JsLocation.empty.append("user")
    }

    init {
        "The 'readAsString' function" - {
            "when called with a receiver of a 'JsString'" - {
                "should return the string value" {
                    val json: JsValue = JsString("abc")
                    val result = json.readAsString(context, LOCATION)
                    result.assertAsSuccess(location = LOCATION, value = "abc")
                }
            }
            "when called with a receiver of a not 'JsString'" - {
                "should return the 'InvalidType' error" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsString(context, LOCATION)
                    result.assertAsFailure(
                        JsResult.Failure.Cause(
                            location = LOCATION,
                            error = JsonErrors.InvalidType(
                                expected = JsValue.Type.STRING,
                                actual = JsValue.Type.BOOLEAN
                            )
                        )
                    )
                }
            }
        }
    }
}
