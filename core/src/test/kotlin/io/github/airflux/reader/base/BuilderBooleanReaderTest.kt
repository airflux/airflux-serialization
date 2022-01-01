package io.github.airflux.reader.base

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.value.JsBoolean
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import kotlin.test.Test

class BuilderBooleanReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val reader = buildBooleanReader(JsonErrors::InvalidType)
    }

    @Test
    fun `Testing reader for the Boolean type (true value)`() {
        val input: JsValue = JsBoolean.valueOf(true)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = true)
    }

    @Test
    fun `Testing reader for the Boolean type (false value)`() {
        val input: JsValue = JsBoolean.valueOf(false)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = false)
    }

    @Test
    fun `Testing reader for the Boolean type (reading from invalid node)`() {
        val input: JsValue = JsString("abc")

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.InvalidType(expected = JsValue.Type.BOOLEAN, actual = JsValue.Type.STRING)
        )
    }
}
