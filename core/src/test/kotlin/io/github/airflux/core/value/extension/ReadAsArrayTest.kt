package io.github.airflux.core.value.extension

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ReadAsArrayTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private const val USER_NAME = "user"
        private val LOCATION = JsLocation.empty.append("user")
        private val reader = { _: JsReaderContext, location: JsLocation, input: JsArray<*> ->
            val result = input.map { (it as JsString).get }
            JsResult.Success(location, result)
        }
    }

    init {
        "The 'readAsArray' function" - {

            "when called with a receiver of a 'JsArray'" - {

                "should return the collection of values" {
                    val json: JsValue = JsArray(JsString(USER_NAME))

                    val result = json.readAsArray(context, LOCATION, reader)

                    result as JsResult.Success
                    result shouldBe JsResult.Success(location = LOCATION, value = listOf(USER_NAME))
                }
            }

            "when called with a receiver of a not 'JsArray'" - {

                "should return the 'InvalidType' error" {
                    val json: JsValue = JsBoolean.valueOf(true)

                    val result = json.readAsArray(context, LOCATION, reader)

                    result as JsResult.Failure
                    result shouldBe JsResult.Failure(
                        location = LOCATION, error = JsonErrors.InvalidType(
                            expected = JsValue.Type.ARRAY,
                            actual = JsValue.Type.BOOLEAN
                        )
                    )
                }
            }
        }
    }
}
