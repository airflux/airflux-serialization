package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsArray
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

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

            result as JsResult.Success
            assertEquals(JsPath.empty, result.path)
            assertEquals(listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
        }

        @Test
        fun `Testing 'readAsList' function (an attribute is not collection)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<List<String>> =
                readAsList(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty, pathError)

                    assertEquals(1, errors.size)
                    val error = errors[0] as JsonErrors.InvalidType
                    assertEquals(JsValue.Type.ARRAY, error.expected)
                    assertEquals(JsValue.Type.STRING, error.actual)
                }
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

            result as JsResult.Failure
            assertEquals(2, result.errors.size)

            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty / 1, pathError)
                    assertEquals(1, errors.size)
                }

            result.errors[1]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty / 2, pathError)
                    assertEquals(1, errors.size)
                }
        }

        @Test
        fun `Testing 'readAsList' function (array is empty)`() {
            val json: JsValue = JsArray<JsString>()

            val result: JsResult<List<String>> =
                readAsList(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result as JsResult.Success
            assertEquals(JsPath.empty, result.path)
            assertEquals(listOf(), result.value)
        }
    }

    @Nested
    inner class SetReader {

        @Test
        fun `Testing 'readAsSet' function`() {
            val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

            val result: JsResult<Set<String>> =
                readAsSet(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result as JsResult.Success
            assertEquals(JsPath.empty, result.path)
            assertEquals(setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE), result.value)
        }

        @Test
        fun `Testing 'readAsSet' function (an attribute is not collection)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<Set<String>> =
                readAsSet(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty, pathError)

                    assertEquals(1, errors.size)
                    val error = errors[0] as JsonErrors.InvalidType
                    assertEquals(JsValue.Type.ARRAY, error.expected)
                    assertEquals(JsValue.Type.STRING, error.actual)
                }
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

            result as JsResult.Failure
            assertEquals(2, result.errors.size)

            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty / 1, pathError)
                    assertEquals(1, errors.size)
                }

            result.errors[1]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty / 2, pathError)
                    assertEquals(1, errors.size)
                }
        }

        @Test
        fun `Testing 'readAsSet' function (array is empty)`() {
            val json: JsValue = JsArray<JsString>()

            val result: JsResult<Set<String>> =
                readAsSet(from = json, using = stringReader, invalidTypeErrorBuilder = JsonErrors::InvalidType)

            result as JsResult.Success
            assertEquals(JsPath.empty, result.path)
            assertEquals(setOf(), result.value)
        }
    }
}
