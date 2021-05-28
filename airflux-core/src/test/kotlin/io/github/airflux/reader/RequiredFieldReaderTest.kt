package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.lookup.JsLookup
import io.github.airflux.path.JsPath
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class RequiredFieldReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val stringReader: JsReader<String> =
            JsReader { input, _ -> JsResult.Success((input as JsString).underlying) }
    }

    @Nested
    inner class FromJsLookup {

        @Test
        fun `Testing 'readRequired' function (a property is found)`() {
            val from: JsLookup = JsLookup.Defined(path = JsPath.empty / "name", JsString(USER_NAME_VALUE))

            val result: JsResult<String> = readRequired(
                from = from,
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = USER_NAME_VALUE)
        }

        @Test
        fun `Testing 'readRequired' function (a property is not found)`() {
            val from: JsLookup = JsLookup.Undefined.PathMissing(path = JsPath.empty / "name")

            val result: JsResult<String> = readRequired(
                from = from,
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsFailure(
                JsPath.empty / "name" to listOf(JsonErrors.PathMissing)
            )
        }

        @Test
        fun `Testing 'readRequired' function (a property is not found, invalid type)`() {
            val from: JsLookup = JsLookup.Undefined.InvalidType(
                path = JsPath.empty / "name",
                expected = JsValue.Type.ARRAY,
                actual = JsValue.Type.STRING
            )

            val result: JsResult<String> = readRequired(
                from = from,
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
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
        fun `Testing 'readRequired' function (a property is found)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String> = readRequired(
                from = json,
                path = JsPath.empty / "name",
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = USER_NAME_VALUE)
        }

        @Test
        fun `Testing 'readRequired' function (a property is not found)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String> = readRequired(
                from = json,
                path = JsPath.empty / "role",
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsFailure(
                JsPath.empty / "role" to listOf(JsonErrors.PathMissing)
            )
        }

        @Test
        fun `Testing 'readRequired' function (a property is not found, invalid type)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<String> = readRequired(
                from = json,
                path = JsPath.empty / "name",
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
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
        fun `Testing 'readRequired' function (a property is found)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String> = readRequired(
                from = json,
                name = "name",
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsSuccess(path = JsPath.empty / "name", value = USER_NAME_VALUE)
        }

        @Test
        fun `Testing 'readRequired' function (a property is not found)`() {
            val json: JsValue = JsObject(
                "name" to JsString(USER_NAME_VALUE)
            )

            val result: JsResult<String> = readRequired(
                from = json,
                name = "role",
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

            result.assertAsFailure(
                JsPath.empty / "role" to listOf(JsonErrors.PathMissing)
            )
        }

        @Test
        fun `Testing 'readRequired' function (a property is not found, invalid type)`() {
            val json: JsValue = JsString(USER_NAME_VALUE)

            val result: JsResult<String> = readRequired(
                from = json,
                name = "name",
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
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
