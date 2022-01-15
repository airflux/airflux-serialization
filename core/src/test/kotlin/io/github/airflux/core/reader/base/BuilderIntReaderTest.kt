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

class BuilderIntReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val reader = buildIntReader(JsonErrors::InvalidType, JsonErrors::ValueCast)
    }

    @Test
    fun `Testing reader for the Int type (min value)`() {
        val input: JsValue = JsNumber.valueOf(Int.MIN_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = Int.MIN_VALUE)
    }

    @Test
    fun `Testing reader for the Int type (max value)`() {
        val input: JsValue = JsNumber.valueOf(Int.MAX_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = Int.MAX_VALUE)
    }

    @Test
    fun `Testing reader for the Int type (reading from invalid node)`() {
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
    fun `Testing reader for the Int type (reading a value that less the allowed range)`() {
        val input: JsValue = JsNumber.valueOf(Long.MIN_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.ValueCast(value = Long.MIN_VALUE.toString(), type = Int::class)
        )
    }

    @Test
    fun `Testing reader for the Int type (reading a value that more the allowed range)`() {
        val input: JsValue = JsNumber.valueOf(Long.MAX_VALUE)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.ValueCast(value = Long.MAX_VALUE.toString(), type = Int::class)
        )
    }

    @Test
    fun `Testing reader for the Int type (reading a value of an invalid format)`() {
        val input: JsValue = JsNumber.valueOf("10.5")!!

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.ValueCast(value = "10.5", type = Int::class)
        )
    }
}
