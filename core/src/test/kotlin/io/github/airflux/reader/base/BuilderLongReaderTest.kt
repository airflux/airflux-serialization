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

class BuilderLongReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val reader = buildLongReader(JsonErrors::InvalidType, JsonErrors::ValueCast)
    }

    @Test
    fun `Testing reader for the Long type (min value)`() {
        val input: JsValue = JsNumber.valueOf(Long.MIN_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = Long.MIN_VALUE)
    }

    @Test
    fun `Testing reader for the Long type (max value)`() {
        val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = Long.MAX_VALUE)
    }

    @Test
    fun `Testing reader for the Long type (reading from invalid node)`() {
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
    fun `Testing reader for the Long type (reading a value of an invalid format)`() {
        val input: JsValue = JsNumber.valueOf("10.5")!!

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.ValueCast(value = "10.5", type = Long::class)
        )
    }
}
