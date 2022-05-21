package io.github.airflux.core.reader.base

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import kotlin.test.Test

class BuilderShortReaderTest {

    companion object {
        private val context = JsReaderContext(
            listOf(
                InvalidTypeErrorBuilder(JsonErrors::InvalidType),
                ValueCastErrorBuilder(JsonErrors::ValueCast)
            )
        )
        private val reader = buildShortReader()
    }

    @Test
    fun `Testing reader for the Short type (min value)`() {
        val input: JsValue = JsNumber.valueOf(Short.MIN_VALUE)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsSuccess(location = JsLocation.empty, value = Short.MIN_VALUE)
    }

    @Test
    fun `Testing reader for the Short type (max value)`() {
        val input: JsValue = JsNumber.valueOf(Short.MAX_VALUE)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsSuccess(location = JsLocation.empty, value = Short.MAX_VALUE)
    }

    @Test
    fun `Testing reader for the Short type (reading from invalid node)`() {
        val input: JsValue = JsString("abc")

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsResult.Failure.Cause(
                location = JsLocation.empty,
                error = JsonErrors.InvalidType(expected = JsValue.Type.NUMBER, actual = JsValue.Type.STRING)
            )
        )
    }

    @Test
    fun `Testing reader for the Short type (reading a value that less the allowed range)`() {
        val input: JsValue = JsNumber.valueOf(Long.MIN_VALUE)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsResult.Failure.Cause(
                location = JsLocation.empty,
                error = JsonErrors.ValueCast(value = Long.MIN_VALUE.toString(), type = Short::class)
            )
        )
    }

    @Test
    fun `Testing reader for the Short type (reading a value that more the allowed range)`() {
        val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsResult.Failure.Cause(
                location = JsLocation.empty,
                error = JsonErrors.ValueCast(value = Long.MAX_VALUE.toString(), type = Short::class)
            )
        )
    }

    @Test
    fun `Testing reader for the Short type (reading a value of an invalid format)`() {
        val input: JsValue = JsNumber.valueOf("10.5")!!

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsResult.Failure.Cause(
                location = JsLocation.empty,
                error = JsonErrors.ValueCast(value = "10.5", type = Short::class)
            )
        )
    }
}
