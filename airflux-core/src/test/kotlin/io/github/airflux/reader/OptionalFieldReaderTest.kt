package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.TestData.USER_NAME_VALUE
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.lookup.JsLookup
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import kotlin.test.Test

class OptionalFieldReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val stringReader: JsReader<String> =
            JsReader { _, location, input ->
                when (input) {
                    is JsString -> JsResult.Success(input.underlying, location)
                    else -> JsResult.Failure(
                        location = location,
                        error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                    )
                }
            }
    }

    @Test
    fun `Testing the readOptional function (a property is found)`() {
        val from: JsLookup = JsLookup.Defined(location = JsLocation.Root / "name", JsString(USER_NAME_VALUE))

        val result: JsResult<String?> =
            readOptional(
                from = from,
                using = stringReader,
                context = context,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

        result.assertAsSuccess(location = JsLocation.Root / "name", value = USER_NAME_VALUE)
    }

    @Test
    fun `Testing the readOptional function (a property is found with value the null)`() {
        val from: JsLookup = JsLookup.Defined(location = JsLocation.Root / "name", JsNull)

        val result: JsResult<String?> =
            readOptional(
                from = from,
                using = stringReader,
                context = context,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

        result.assertAsFailure(
            "name" bind JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NULL)
        )
    }

    @Test
    fun `Testing the readOptional function (a property is not found, returning value the null)`() {
        val from: JsLookup = JsLookup.Undefined.PathMissing(location = JsLocation.Root / "name")

        val result: JsResult<String?> =
            readOptional(
                from = from,
                using = stringReader,
                context = context,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

        result.assertAsSuccess(location = JsLocation.Root / "name", value = null)
    }

    @Test
    fun `Testing the readOptional function (a property is not found, invalid type)`() {
        val from: JsLookup = JsLookup.Undefined.InvalidType(
            location = JsLocation.Root / "name",
            expected = JsValue.Type.ARRAY,
            actual = JsValue.Type.STRING
        )

        val result: JsResult<String?> =
            readOptional(
                from = from,
                using = stringReader,
                context = context,
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )

        result.assertAsFailure(
            "name" bind JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
        )
    }
}
