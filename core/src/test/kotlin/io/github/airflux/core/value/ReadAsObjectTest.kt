package io.github.airflux.core.value

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsObjectTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private const val USER_NAME = "user"
        private val LOCATION = JsLocation.empty.append("user")
        private val reader = { _: JsReaderContext, location: JsLocation, input: JsObject ->
            val name = input["name"] as JsString
            JsResult.Success(location, DTO(name = name.get))
        }
    }

    init {
        "The 'readAsObject' function" - {
            "when called with a receiver of a 'JsObject'" - {
                "should return the DTO" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME))
                    val result = json.readAsObject(context, LOCATION, reader)
                    result.assertAsSuccess(location = LOCATION, value = DTO(name = USER_NAME))
                }
            }
            "when called with a receiver of a not 'JsObject'" - {
                "should return the 'InvalidType' error" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsObject(context, LOCATION, reader)
                    result.assertAsFailure(
                        JsResult.Failure.Cause(
                            location = LOCATION,
                            error = JsonErrors.InvalidType(
                                expected = JsValue.Type.OBJECT,
                                actual = JsValue.Type.BOOLEAN
                            )
                        )
                    )
                }
            }
        }
    }

    private data class DTO(val name: String)
}
