package io.github.airflux.core.reader

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.core.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.core.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

class CollectionFieldReaderTest : FreeSpec() {

    companion object {
        private val context = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private val stringReader: JsReader<String> = JsReader { _, location, input ->
            if (input is JsString)
                JsResult.Success(location, input.get)
            else JsResult.Failure(
                location = location,
                error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
            )
        }
    }

    init {

        "The readAsList function" - {

            "should return the result with a non-empty value if a collection is not empty" {
                val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

                val result: JsResult<List<String>> =
                    readAsList(context = context, location = JsLocation.empty, from = json, using = stringReader)

                result shouldBe JsResult.Success(
                    location = JsLocation.empty,
                    value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                )
            }

            "should return the result with an empty value if a collection is empty" {
                val json: JsValue = JsArray<JsString>()

                val result: JsResult<List<String>> =
                    readAsList(context = context, location = JsLocation.empty, from = json, using = stringReader)

                result shouldBe JsResult.Success(location = JsLocation.empty, value = emptyList())
            }

            "should return the invalid type error if the parameter 'from' is not JsArray" {
                val json: JsValue = JsString(USER_NAME_VALUE)

                val result: JsResult<List<String>> =
                    readAsList(context = context, location = JsLocation.empty, from = json, using = stringReader)

                result shouldBe JsResult.Failure(
                    JsLocation.empty,
                    JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
                )
            }

            "should return the invalid type error if a collection has inconsistent content" {
                val json: JsValue = JsArray(
                    JsString(FIRST_PHONE_VALUE),
                    JsNumber.valueOf(10),
                    JsBoolean.True,
                    JsString(SECOND_PHONE_VALUE)
                )

                val result: JsResult<List<String>> =
                    readAsList(context = context, location = JsLocation.empty, from = json, using = stringReader)

                result as JsResult.Failure
                result.causes shouldContainAll listOf(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty.append(1),
                        error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NUMBER)
                    ),
                    JsResult.Failure.Cause(
                        location = JsLocation.empty.append(2),
                        error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                    )
                )
            }
        }

        "The readAsSet function" - {

            "should return the result with a non-empty value if a collection is not empty" {
                val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

                val result: JsResult<Set<String>> =
                    readAsSet(context = context, location = JsLocation.empty, from = json, using = stringReader)

                result shouldBe JsResult.Success(
                    location = JsLocation.empty,
                    value = setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                )
            }

            "should return the result with an empty value if a collection is empty" {
                val json: JsValue = JsArray<JsString>()

                val result: JsResult<Set<String>> =
                    readAsSet(context = context, location = JsLocation.empty, from = json, using = stringReader)

                result shouldBe JsResult.Success(location = JsLocation.empty, value = emptySet())
            }

            "should return the invalid type error if the parameter 'from' is not JsArray" {
                val json: JsValue = JsString(USER_NAME_VALUE)

                val result: JsResult<Set<String>> =
                    readAsSet(context = context, location = JsLocation.empty, from = json, using = stringReader)

                result shouldBe JsResult.Failure(
                    JsLocation.empty,
                    JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
                )
            }

            "should return the invalid type error if a collection has inconsistent content" {
                val json: JsValue = JsArray(
                    JsString(FIRST_PHONE_VALUE),
                    JsNumber.valueOf(10),
                    JsBoolean.True,
                    JsString(SECOND_PHONE_VALUE)
                )

                val result: JsResult<Set<String>> =
                    readAsSet(context = context, location = JsLocation.empty, from = json, using = stringReader)

                result as JsResult.Failure
                result.causes shouldContainAll listOf(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty.append(1),
                        error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NUMBER)
                    ),
                    JsResult.Failure.Cause(
                        location = JsLocation.empty.append(2),
                        error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                    )
                )
            }
        }
    }
}
