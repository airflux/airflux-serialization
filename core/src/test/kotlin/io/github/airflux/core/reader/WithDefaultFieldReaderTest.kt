package io.github.airflux.core.reader

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.TestData.USER_NAME_VALUE
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import kotlin.test.Test

class WithDefaultFieldReaderTest {

    companion object {
        private val context = JsReaderContext()
        private const val DEFAULT_VALUE = "Default value"
        private val stringReader: JsReader<String> =
            JsReader { _, location, input -> JsResult.Success((input as JsString).get, location) }
        private val defaultValue = { DEFAULT_VALUE }
    }

    @Test
    fun `Testing the readWithDefault function (a property is found)`() {
        val from: JsLookup = JsLookup.Defined(location = JsLocation.Root / "name", JsString(USER_NAME_VALUE))

        val result: JsResult<String> = readWithDefault(
            from = from,
            using = stringReader,
            defaultValue = defaultValue,
            context = context,
            invalidTypeErrorBuilder = JsonErrors::InvalidType
        )

        result.assertAsSuccess(location = JsLocation.Root / "name", value = USER_NAME_VALUE)
    }

    @Test
    fun `Testing the readWithDefault function (a property is found with value the null, returning default value)`() {
        val from: JsLookup = JsLookup.Defined(location = JsLocation.Root / "name", JsNull)

        val result: JsResult<String> = readWithDefault(
            from = from,
            using = stringReader,
            defaultValue = defaultValue,
            context = context,
            invalidTypeErrorBuilder = JsonErrors::InvalidType
        )

        result.assertAsSuccess(location = JsLocation.Root / "name", value = DEFAULT_VALUE)
    }

    @Test
    fun `Testing the readWithDefault function (a property is not found, returning default value)`() {
        val from: JsLookup = JsLookup.Undefined.PathMissing(location = JsLocation.Root / "name")

        val result: JsResult<String> = readWithDefault(
            from = from,
            using = stringReader,
            defaultValue = defaultValue,
            context = context,
            invalidTypeErrorBuilder = JsonErrors::InvalidType
        )

        result.assertAsSuccess(location = JsLocation.Root / "name", value = DEFAULT_VALUE)
    }

    @Test
    fun `Testing the readWithDefault function (a property is not found, invalid type)`() {
        val from: JsLookup = JsLookup.Undefined.InvalidType(
            location = JsLocation.Root / "name",
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
