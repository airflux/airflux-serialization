package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.DEFAULT_VALUE
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.reader.NullableWithDefaultPathReader.Companion.nullableWithDefault
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NullableWithDefaultPathReaderTest {

    companion object {
        private val stringReader: JsReader<String> =
            JsReader { input -> JsResult.Success((input as JsString).underlying) }
        private val defaultValue = { DEFAULT_VALUE }
    }

    @Nested
    inner class FromJsLookup {

        @Test
        fun `Testing 'withDefault' function (an attribute is found)`() {
            val from: JsLookup = JsLookup.Defined(path = JsPath.empty / "name", JsString(USER_NAME_VALUE))

            val result: JsResult<String?> = nullableWithDefault(
                from = from,
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Success
            assertEquals(JsPath.empty / "name", result.path)
            assertEquals(USER_NAME_VALUE, result.value)
        }

        @Test
        fun `Testing 'withDefault' function (an attribute is found with value 'null', returning default value)`() {
            val from: JsLookup = JsLookup.Defined(path = JsPath.empty / "name", JsNull)

            val result: JsResult<String?> = nullableWithDefault(
                from = from,
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Success
            assertEquals(JsPath.empty / "name", result.path)
            assertNull(result.value)
        }

        @Test
        fun `Testing 'withDefault' function (an attribute is not found, returning default value)`() {
            val from: JsLookup = JsLookup.Undefined.PathMissing(path = JsPath.empty / "name")

            val result: JsResult<String?> = nullableWithDefault(
                from = from,
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Success
            assertEquals(JsPath.empty / "name", result.path)
            assertEquals(DEFAULT_VALUE, result.value)
        }

        @Test
        fun `Testing 'withDefault' function (an attribute is not found, invalid type)`() {
            val from: JsLookup = JsLookup.Undefined.InvalidType(
                path = JsPath.empty / "name",
                expected = JsValue.Type.ARRAY,
                actual = JsValue.Type.STRING
            )

            val result: JsResult<String?> = nullableWithDefault(
                from = from,
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty / "name", pathError)

                    assertEquals(1, errors.size)
                    val error = errors[0] as JsonErrors.InvalidType
                    assertEquals(JsValue.Type.ARRAY, error.expected)
                    assertEquals(JsValue.Type.STRING, error.actual)
                }
        }
    }

    @Nested
    inner class FromJsValueByPath {

        @Test
        fun `Testing 'withDefault' function (an attribute is found)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
            val path = JsPath.empty / "name"

            val result: JsResult<String?> = nullableWithDefault(
                from = json,
                path = path,
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Success
            assertEquals(JsPath.empty / "name", result.path)
            assertEquals(USER_NAME_VALUE, result.value)
        }

        @Test
        fun `Testing 'withDefault' function (an attribute is found with value 'null', returning default value)`() {
            val json: JsValue = JsObject(
                "role" to JsNull
            )
            val path = JsPath.empty / "role"

            val result: JsResult<String?> = nullableWithDefault(
                from = json,
                path = path,
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Success
            assertEquals(JsPath.empty / "role", result.path)
            assertNull(result.value)
        }

        @Test
        fun `Testing 'withDefault' function (an attribute is not found, returning default value)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )
            val path = JsPath.empty / "role"

            val result: JsResult<String?> = nullableWithDefault(
                from = json,
                path = path,
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Success
            assertEquals(JsPath.empty / "role", result.path)
            assertEquals(DEFAULT_VALUE, result.value)
        }

        @Test
        fun `Testing 'withDefault' function (an attribute is not found, invalid type)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)
            val path = JsPath.empty / "name"

            val result: JsResult<String?> = nullableWithDefault(
                from = json,
                path = path,
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty, pathError)

                    assertEquals(1, errors.size)
                    val error = errors[0] as JsonErrors.InvalidType
                    assertEquals(JsValue.Type.OBJECT, error.expected)
                    assertEquals(JsValue.Type.STRING, error.actual)
                }
        }
    }

    @Nested
    inner class FromJsValueByName {

        @Test
        fun `Testing 'withDefault' function (an attribute is found)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String?> = nullableWithDefault(
                from = json,
                name = "name",
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Success
            assertEquals(JsPath.empty / "name", result.path)
            assertEquals(USER_NAME_VALUE, result.value)
        }

        @Test
        fun `Testing 'withDefault' function (an attribute is found with value 'null', returning default value)`() {
            val json: JsValue = JsObject(
                "role" to JsNull
            )

            val result: JsResult<String?> = nullableWithDefault(
                from = json,
                name = "role",
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Success
            assertEquals(JsPath.empty / "role", result.path)
            assertNull(result.value)
        }

        @Test
        fun `Testing 'withDefault' function (an attribute is not found, returning default value)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String?> = nullableWithDefault(
                from = json,
                name = "role",
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Success
            assertEquals(JsPath.empty / "role", result.path)
            assertEquals(DEFAULT_VALUE, result.value)
        }

        @Test
        fun `Testing 'withDefault' function (an attribute is not found, invalid type)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<String?> = nullableWithDefault(
                from = json,
                name = "name",
                using = stringReader,
                defaultValue = defaultValue,
                errorInvalidType = JsonErrors::InvalidType
            )

            result as JsResult.Failure
            assertEquals(1, result.errors.size)

            result.errors[0]
                .also { (pathError, errors) ->
                    assertEquals(JsPath.empty, pathError)

                    assertEquals(1, errors.size)
                    val error = errors[0] as JsonErrors.InvalidType
                    assertEquals(JsValue.Type.OBJECT, error.expected)
                    assertEquals(JsValue.Type.STRING, error.actual)
                }
        }
    }
}
