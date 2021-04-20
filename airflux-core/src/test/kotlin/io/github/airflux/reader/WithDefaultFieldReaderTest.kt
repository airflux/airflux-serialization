package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class WithDefaultFieldReaderTest {

    companion object {
        private const val DEFAULT_VALUE = "Default value"
        private val stringReader: JsReader<String> =
            JsReader { input -> JsResult.Success((input as JsString).underlying) }
        private val defaultValue = { DEFAULT_VALUE }
    }

    @Nested
    inner class FromJsLookup {

        @Test
        fun `Testing 'readWithDefault' function (a property is found)`() {
            val from: JsLookup = JsLookup.Defined(path = JsPath.empty / "name", JsString(USER_NAME_VALUE))

            val result: JsResult<String> = readWithDefault(
                from = from,
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = USER_NAME_VALUE)
        }

        @Test
        fun `Testing 'readWithDefault' function (a property is found with value 'null', returning default value)`() {
            val from: JsLookup = JsLookup.Defined(path = JsPath.empty / "name", JsNull)

            val result: JsResult<String> = readWithDefault(
                from = from,
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = DEFAULT_VALUE)
        }

        @Test
        fun `Testing 'readWithDefault' function (a property is not found, returning default value)`() {
            val from: JsLookup = JsLookup.Undefined.PathMissing(path = JsPath.empty / "name")

            val result: JsResult<String> = readWithDefault(
                from = from,
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = DEFAULT_VALUE)
        }

        @Test
        fun `Testing 'readWithDefault' function (a property is not found, invalid type)`() {
            val from: JsLookup = JsLookup.Undefined.InvalidType(
                path = JsPath.empty / "name",
                expected = JsValue.Type.ARRAY,
                actual = JsValue.Type.STRING
            )

            val result: JsResult<String> = readWithDefault(
                from = from,
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsFailure(
                JsPath.empty / "name" to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
                )
            )
        }
    }

    @Nested
    inner class FromJsValueByPath {

        @Test
        fun `Testing 'readWithDefault' function (a property is found)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String> = readWithDefault(
                from = json,
                path = JsPath.empty / "name",
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = USER_NAME_VALUE)
        }

        @Test
        fun `Testing 'readWithDefault' function (a property is found with value 'null', returning default value)`() {
            val json: JsValue = JsObject(
                "name" to JsNull
            )

            val result: JsResult<String> = readWithDefault(
                from = json,
                path = JsPath.empty / "name",
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = DEFAULT_VALUE)
        }

        @Test
        fun `Testing 'readWithDefault' function (a property is not found, returning default value)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String> = readWithDefault(
                from = json,
                path = JsPath.empty / "role",
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "role", value = DEFAULT_VALUE)
        }

        @Test
        fun `Testing 'readWithDefault' function (a property is not found, invalid type)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<String> = readWithDefault(
                from = json,
                path = JsPath.empty / "name",
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsFailure(
                JsPath.empty to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
                )
            )
        }
    }

    @Nested
    inner class FromJsValueByName {

        @Test
        fun `Testing 'readWithDefault' function (a property is found)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String> = readWithDefault(
                from = json,
                name = "name",
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = USER_NAME_VALUE)
        }

        @Test
        fun `Testing 'readWithDefault' function (a property is found with value 'null', returning default value)`() {
            val json: JsValue = JsObject(
                "name" to JsNull
            )

            val result: JsResult<String> = readWithDefault(
                from = json,
                name = "name",
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = DEFAULT_VALUE)
        }

        @Test
        fun `Testing 'readWithDefault' function (a property is not found, returning default value)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String> = readWithDefault(
                from = json,
                name = "role",
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "role", value = DEFAULT_VALUE)
        }

        @Test
        fun `Testing 'readWithDefault' function (a property is not found, invalid type)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<String> = readWithDefault(
                from = json,
                name = "name",
                using = stringReader,
                defaultValue = defaultValue,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsFailure(
                JsPath.empty to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
                )
            )
        }
    }
}
