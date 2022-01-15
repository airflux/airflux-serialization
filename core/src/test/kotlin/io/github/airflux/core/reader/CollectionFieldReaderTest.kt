package io.github.airflux.core.reader

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.TestData.FIRST_PHONE_VALUE
import io.github.airflux.core.common.TestData.SECOND_PHONE_VALUE
import io.github.airflux.core.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class CollectionFieldReaderTest {

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

    @Nested
    inner class ListReader {

        @Test
        fun `Testing the readAsList function`() {
            val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

            val result: JsResult<List<String>> =
                readAsList(
                    context = context,
                    location = JsLocation.Root,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

            result.assertAsSuccess(location = JsLocation.Root, value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))
        }

        @Test
        fun `Testing the readAsList function (a property is not collection)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<List<String>> =
                readAsList(
                    context = context,
                    location = JsLocation.Root,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

            result.assertAsFailure(
                JsLocation.Root bind JsonErrors.InvalidType(
                    expected = JsValue.Type.ARRAY,
                    actual = JsValue.Type.STRING
                )
            )
        }

        @Test
        fun `Testing the readAsList function (collection with inconsistent content)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE),
                JsNumber.valueOf(10),
                JsBoolean.True,
                JsString(SECOND_PHONE_VALUE)
            )

            val result: JsResult<List<String>> =
                readAsList(
                    context = context,
                    location = JsLocation.Root,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

            result.assertAsFailure(
                1 bind JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NUMBER),
                2 bind JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
            )
        }

        @Test
        fun `Testing the readAsList function (array is empty)`() {
            val json: JsValue = JsArray<JsString>()

            val result: JsResult<List<String>> =
                readAsList(
                    context = context,
                    location = JsLocation.Root,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

            result.assertAsSuccess(location = JsLocation.Root, value = emptyList())
        }
    }

    @Nested
    inner class SetReader {

        @Test
        fun `Testing the readAsSet function`() {
            val json: JsValue = JsArray(JsString(FIRST_PHONE_VALUE), JsString(SECOND_PHONE_VALUE))

            val result: JsResult<Set<String>> =
                readAsSet(
                    context = context,
                    location = JsLocation.Root,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

            result.assertAsSuccess(location = JsLocation.Root, value = setOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE))
        }

        @Test
        fun `Testing the readAsSet function (a property is not collection)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<Set<String>> =
                readAsSet(
                    context = context,
                    location = JsLocation.Root,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

            result.assertAsFailure(
                JsLocation.Root bind JsonErrors.InvalidType(
                    expected = JsValue.Type.ARRAY,
                    actual = JsValue.Type.STRING
                )
            )
        }

        @Test
        fun `Testing the readAsSet function (collection with inconsistent content)`() {
            val json: JsValue = JsArray(
                JsString(FIRST_PHONE_VALUE),
                JsNumber.valueOf(10),
                JsBoolean.True,
                JsString(SECOND_PHONE_VALUE)
            )

            val result: JsResult<Set<String>> =
                readAsSet(
                    context = context,
                    location = JsLocation.Root,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )


            result.assertAsFailure(
                1 bind JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NUMBER),
                2 bind JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
            )
        }

        @Test
        fun `Testing the readAsSet function (array is empty)`() {
            val json: JsValue = JsArray<JsString>()

            val result: JsResult<Set<String>> =
                readAsSet(
                    context = context,
                    location = JsLocation.Root,
                    from = json,
                    using = stringReader,
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )

            result.assertAsSuccess(location = JsLocation.Root, value = emptySet())
        }
    }
}
