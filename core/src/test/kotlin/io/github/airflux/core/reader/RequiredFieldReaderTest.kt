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
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import kotlin.test.Test

class RequiredFieldReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val stringReader: JsReader<String> =
            JsReader { _, location, input -> JsResult.Success((input as JsString).get, location) }
    }

    @Test
    fun `Testing the readRequired function (a property is found)`() {
        val from: JsLookup = JsLookup.Defined(location = JsLocation.empty.append("name"), JsString(USER_NAME_VALUE))

        val result: JsResult<String> = readRequired(
            from = from,
            using = stringReader,
            context = context,
            pathMissingErrorBuilder = { JsonErrors.PathMissing },
            invalidTypeErrorBuilder = JsonErrors::InvalidType
        )

        result.assertAsSuccess(location = JsLocation.empty.append("name"), value = USER_NAME_VALUE)
    }

    @Test
    fun `Testing the readRequired function (a property is not found)`() {
        val from: JsLookup = JsLookup.Undefined.PathMissing(location = JsLocation.empty.append("name"))

        val result: JsResult<String> = readRequired(
            from = from,
            using = stringReader,
            context = context,
            pathMissingErrorBuilder = { JsonErrors.PathMissing },
            invalidTypeErrorBuilder = JsonErrors::InvalidType
        )

        result.assertAsFailure("name" bind JsonErrors.PathMissing)
    }

    @Test
    fun `Testing the readRequired function (a property is not found, invalid type)`() {
        val from: JsLookup = JsLookup.Undefined.InvalidType(
            location = JsLocation.empty.append("name"),
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
            "name" bind JsonErrors.InvalidType(expected = JsValue.Type.ARRAY, actual = JsValue.Type.STRING)
        )
    }
}
