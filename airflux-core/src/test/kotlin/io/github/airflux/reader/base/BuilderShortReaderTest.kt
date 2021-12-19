package io.github.airflux.reader.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.value.JsNumber
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import kotlin.test.Test

class BuilderShortReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val reader = buildShortReader(JsonErrors::InvalidType, JsonErrors::ValueCast)
    }

    @Test
    fun `Testing reader for the Short type (min value)`() {
        val input: JsValue = JsNumber.valueOf(Short.MIN_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = Short.MIN_VALUE)
    }

    @Test
    fun `Testing reader for the Short type (max value)`() {
        val input: JsValue = JsNumber.valueOf(Short.MAX_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = Short.MAX_VALUE)
    }

    @Test
    fun `Testing reader for the Short type (reading from invalid node)`() {
        val input: JsValue = JsString("abc")

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.InvalidType(
                expected = JsValue.Type.NUMBER,
                actual = JsValue.Type.STRING
            )
        )
    }

    @Test
    fun `Testing reader for the Short type (reading a value that less the allowed range)`() {
        val input: JsValue = JsNumber.valueOf(Long.MIN_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.ValueCast(value = Long.MIN_VALUE.toString(), type = Short::class)
        )
    }

    @Test
    fun `Testing reader for the Short type (reading a value that more the allowed range)`() {
        val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.ValueCast(value = Long.MAX_VALUE.toString(), type = Short::class)
        )
    }

    @Test
    fun `Testing reader for the Short type (reading a value of an invalid format)`() {
        val input: JsValue = JsNumber.valueOf("10.5")!!

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.ValueCast(value = "10.5", type = Short::class)
        )
    }
}
