package io.github.airflux.core.value.extension

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsObjectTest : FreeSpec() {

    companion object {
        private const val USER_NAME = "user"
        private val LOCATION = JsLocation.Root / "user"
        private val reader = { location: JsLocation, input: JsObject ->
            val name = input["name"] as JsString
            JsResult.Success(DTO(name = name.get), location)
        }
    }

    init {
        "The 'readAsObject' function" - {
            "when called with a receiver of a 'JsObject'" - {
                "should return the DTO" {
                    val json: JsValue = JsObject("name" to JsString(USER_NAME))
                    val result = json.readAsObject(LOCATION, JsonErrors::InvalidType, reader)
                    result.assertAsSuccess(location = LOCATION, value = DTO(name = USER_NAME))

                }
            }
            "when called with a receiver of a not 'JsObject'" - {
                "should return the 'InvalidType' error" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsObject(LOCATION, JsonErrors::InvalidType, reader)
                    result.assertAsFailure(
                        LOCATION bind JsonErrors.InvalidType(
                            expected = JsValue.Type.OBJECT,
                            actual = JsValue.Type.BOOLEAN
                        )
                    )
                }
            }
        }
    }

    private data class DTO(val name: String)
}
