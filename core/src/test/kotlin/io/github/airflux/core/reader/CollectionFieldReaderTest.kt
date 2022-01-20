package io.github.airflux.core.reader

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.core.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.core.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsErrors
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
        private val context = JsReaderContext()
        private val stringReader: JsReader<String> = JsReader { _, location, input ->
            when (input) {
                is JsString -> JsResult.Success(input.get, location)
                else -> JsResult.Failure(
                    location = location,
                    error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                )
            }
        }
    }

    init {

        "A 'readAsList' function" - {

            "should return non-empty result if collection is non-empty" {
                val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

                val result: JsResult<List<String>> = readAsList(
                    context = context,
                    location = JsLocation.empty,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result shouldBe JsResult.Success(
                    location = JsLocation.empty,
                    value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                )
            }

            "should return empty result if collection is empty" {
                val json: JsValue = JsArray<JsString>()

                val result: JsResult<List<String>> = readAsList(
                    context = context,
                    location = JsLocation.empty,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result shouldBe JsResult.Success(location = JsLocation.empty, value = emptyList())
            }

            "should return error if parameter 'from' is not JsArray" {
                val json: JsValue = JsString(USER_NAME_VALUE)

                val result: JsResult<List<String>> = readAsList(
                    context = context,
                    location = JsLocation.empty,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result as JsResult.Failure
                result shouldBe JsResult.Failure(
                    JsLocation.empty,
                    JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
                )
            }

            "should return error if collection with inconsistent content" {
                val json: JsValue = JsArray(
                    JsString(FIRST_PHONE_VALUE),
                    JsNumber.valueOf(10),
                    JsBoolean.True,
                    JsString(SECOND_PHONE_VALUE)
                )

                val result: JsResult<List<String>> = readAsList(
                    context = context,
                    location = JsLocation.empty,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result as JsResult.Failure
                result.causes shouldContainAll listOf(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty.append(1),
                        errors = JsErrors.of(
                            JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NUMBER)
                        )
                    ),
                    JsResult.Failure.Cause(
                        location = JsLocation.empty.append(2),
                        errors = JsErrors.of(
                            JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                        )
                    )
                )
            }
        }

        "A 'readAsSet' method" - {

            "should return non-empty result if collection is non-empty" {
                val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

                val result: JsResult<Set<String>> = readAsSet(
                    context = context,
                    location = JsLocation.empty,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result shouldBe JsResult.Success(
                    location = JsLocation.empty,
                    value = setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE)
                )
            }

            "should return empty result if collection is empty" {
                val json: JsValue = JsArray<JsString>()

                val result: JsResult<Set<String>> = readAsSet(
                    context = context,
                    location = JsLocation.empty,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result shouldBe JsResult.Success(location = JsLocation.empty, value = emptySet())
            }

            "should return error if parameter 'from' is not JsArray" {
                val json: JsValue = JsString(USER_NAME_VALUE)

                val result: JsResult<Set<String>> = readAsSet(
                    context = context,
                    location = JsLocation.empty,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result as JsResult.Failure
                result shouldBe JsResult.Failure(
                    JsLocation.empty,
                    JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
                )
            }

            "should return error if collection with inconsistent content" {
                val json: JsValue = JsArray(
                    JsString(FIRST_PHONE_VALUE),
                    JsNumber.valueOf(10),
                    JsBoolean.True,
                    JsString(SECOND_PHONE_VALUE)
                )

                val result: JsResult<Set<String>> = readAsSet(
                    context = context,
                    location = JsLocation.empty,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

                result as JsResult.Failure
                result.causes shouldContainAll listOf(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty.append(1),
                        errors = JsErrors.of(
                            JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NUMBER)
                        )
                    ),
                    JsResult.Failure.Cause(
                        location = JsLocation.empty.append(2),
                        errors = JsErrors.of(
                            JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                        )
                    )
                )
            }
        }
    }
}
