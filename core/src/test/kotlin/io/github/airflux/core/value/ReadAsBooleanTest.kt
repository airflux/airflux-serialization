package io.github.airflux.core.value

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsBooleanTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private val LOCATION: JsLocation = JsLocation.empty.append("user")
    }

    init {
        "The 'readAsBoolean' function" - {
            "when called with a receiver of a 'JsBoolean'" - {
                "should return the boolean value" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsBoolean(CONTEXT, LOCATION)
                    result.assertAsSuccess(location = LOCATION, value = true)
                }
            }
            "when called with a receiver of a not 'JsBoolean'" - {
                "should return the 'InvalidType' error" {
                    val json = JsString("abc")
                    val result = json.readAsBoolean(CONTEXT, LOCATION)
                    result.assertAsFailure(
                        JsResult.Failure.Cause(
                            location = LOCATION,
                            error = JsonErrors.InvalidType(
                                expected = JsValue.Type.BOOLEAN,
                                actual = JsValue.Type.STRING
                            )
                        )
                    )
                }
            }
        }
    }
}
