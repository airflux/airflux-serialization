package io.github.airflux.core.reader.base

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import kotlin.test.Test

class BuilderByteReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val reader = buildByteReader(JsonErrors::InvalidType, JsonErrors::ValueCast)
    }

    @Test
    fun `Testing reader for the Byte type (min value)`() {
        val input: JsValue = JsNumber.valueOf(Byte.MIN_VALUE)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsSuccess(location = JsLocation.empty, value = Byte.MIN_VALUE)
    }

    @Test
    fun `Testing reader for the Byte type (max value)`() {
        val input: JsValue = JsNumber.valueOf(Byte.MAX_VALUE)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsSuccess(location = JsLocation.empty, value = Byte.MAX_VALUE)
    }

    @Test
    fun `Testing reader for the Byte type (reading from invalid node)`() {
        val input: JsValue = JsString("abc")

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsLocation.empty bind JsonErrors.InvalidType(expected = JsValue.Type.NUMBER, actual = JsValue.Type.STRING)
        )
    }

    @Test
    fun `Testing reader for the Byte type (reading a value that less the allowed range)`() {
        val input: JsValue = JsNumber.valueOf(Long.MIN_VALUE)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsLocation.empty bind JsonErrors.ValueCast(value = Long.MIN_VALUE.toString(), type = Byte::class)
        )
    }

    @Test
    fun `Testing reader for the Byte type (reading a value that more the allowed range)`() {
        val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsLocation.empty bind JsonErrors.ValueCast(value = Long.MAX_VALUE.toString(), type = Byte::class)
        )
    }

    @Test
    fun `Testing reader for the Byte type (reading a value of an invalid format)`() {
        val input: JsValue = JsNumber.valueOf("10.5")!!

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsLocation.empty bind JsonErrors.ValueCast(value = "10.5", type = Byte::class)
        )
    }
}
