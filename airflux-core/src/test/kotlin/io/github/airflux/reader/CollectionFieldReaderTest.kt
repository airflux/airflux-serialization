package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class CollectionFieldReaderTest {

    companion object {
        private val stringReader: JsReader<String> = JsReader { input ->
            when (input) {
                is JsString -> JsResult.Success(input.underlying)
                else -> JsResult.Failure(
                    error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                )
            }
        }
    }

    @Nested
    inner class ListReader {

        @Test
        fun `Testing 'readAsList' function`() {
            val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

            val result: JsResult<List<String>> =
                readAsList(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result.assertAsSuccess(path = JsPath.empty, value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))
        }

        @Test
        fun `Testing 'readAsList' function (a property is not collection)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<List<String>> =
                readAsList(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result.assertAsFailure(
                JsPath.empty to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
                )
            )
        }

        @Test
        fun `Testing 'readAsList' function (collection with inconsistent content)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE),
                JsNumber.valueOf(10),
                JsBoolean.True,
                JsString(SECOND_PHONE_VALUE)
            )

            val result: JsResult<List<String>> =
                readAsList(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result.assertAsFailure(
                JsPath.empty / 1 to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NUMBER)
                ),
                JsPath.empty / 2 to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                )
            )
        }

        @Test
        fun `Testing 'readAsList' function (array is empty)`() {
            val json: JsValue = JsArray<JsString>()

            val result: JsResult<List<String>> =
                readAsList(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result.assertAsSuccess(path = JsPath.empty, value = emptyList())
        }
    }

    @Nested
    inner class SetReader {

        @Test
        fun `Testing 'readAsSet' function`() {
            val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

            val result: JsResult<Set<String>> =
                readAsSet(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result.assertAsSuccess(path = JsPath.empty, value = setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))
        }

        @Test
        fun `Testing 'readAsSet' function (a property is not collection)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<Set<String>> =
                readAsSet(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result.assertAsFailure(
                JsPath.empty to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
                )
            )
        }

        @Test
        fun `Testing 'readAsSet' function (collection with inconsistent content)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE),
                JsNumber.valueOf(10),
                JsBoolean.True,
                JsString(SECOND_PHONE_VALUE)
            )

            val result: JsResult<Set<String>> =
                readAsSet(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)


            result.assertAsFailure(
                JsPath.empty / 1 to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NUMBER)
                ),
                JsPath.empty / 2 to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
                )
            )
        }

        @Test
        fun `Testing 'readAsSet' function (array is empty)`() {
            val json: JsValue = JsArray<JsString>()

            val result: JsResult<Set<String>> =
                readAsSet(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result.assertAsSuccess(path = JsPath.empty, value = emptySet())
        }
    }
}
