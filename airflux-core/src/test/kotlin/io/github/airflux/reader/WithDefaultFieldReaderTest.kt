package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.lookup.JsLookup
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import kotlin.test.Test

class WithDefaultFieldReaderTest {

    companion object {
        private val context = JsReaderContext()
        private const val DEFAULT_VALUE = "Default value"
        private val stringReader: JsReader<String> =
            JsReader { _, path, input -> JsResult.Success((input as JsString).underlying, path) }
        private val defaultValue = { DEFAULT_VALUE }
    }

    @Test
    fun `Testing the readWithDefault function (a property is found)`() {
        val from: JsLookup = JsLookup.Defined(path = JsResultPath.Root / "name", JsString(USER_NAME_VALUE))

        val result: JsResult<String> = readWithDefault(
            from = from,
            using = stringReader,
            defaultValue = defaultValue,
            context = context,
            invalidTypeErrorBuilder = JsonErrors::InvalidType
        )

        result.assertAsSuccess(path = JsResultPath.Root / "name", value = USER_NAME_VALUE)
    }

    @Test
    fun `Testing the readWithDefault function (a property is found with value the null, returning default value)`() {
        val from: JsLookup = JsLookup.Defined(path = JsResultPath.Root / "name", JsNull)

        val result: JsResult<String> = readWithDefault(
            from = from,
            using = stringReader,
            defaultValue = defaultValue,
            context = context,
            invalidTypeErrorBuilder = JsonErrors::InvalidType
        )

        result.assertAsSuccess(path = JsResultPath.Root / "name", value = DEFAULT_VALUE)
    }

    @Test
    fun `Testing the readWithDefault function (a property is not found, returning default value)`() {
        val from: JsLookup = JsLookup.Undefined.PathMissing(path = JsResultPath.Root / "name")

        val result: JsResult<String> = readWithDefault(
            from = from,
            using = stringReader,
            defaultValue = defaultValue,
            context = context,
            invalidTypeErrorBuilder = JsonErrors::InvalidType
        )

        result.assertAsSuccess(path = JsResultPath.Root / "name", value = DEFAULT_VALUE)
    }

    @Test
    fun `Testing the readWithDefault function (a property is not found, invalid type)`() {
        val from: JsLookup = JsLookup.Undefined.InvalidType(
            path = JsResultPath.Root / "name",
            expected = JsValue.Type.ARRAY,
            actual = JsValue.Type.STRING
        )

        val result: JsResult<String> = readWithDefault(
            from = from,
            using = stringReader,
            defaultValue = defaultValue,
            context = context,
            invalidTypeErrorBuilder = JsonErrors::InvalidType
        )

        result.assertAsFailure(
            "name" bind JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
        )
    }
}
