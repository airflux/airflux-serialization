package io.github.airflux.core.reader.base

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import kotlin.test.Test

class BuilderBooleanReaderTest {

    companion object {
        private val context = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
        private val reader = buildBooleanReader()
    }

    @Test
    fun `Testing reader for the Boolean type (true value)`() {
        val input: JsValue = JsBoolean.valueOf(true)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsSuccess(location = JsLocation.empty, value = true)
    }

    @Test
    fun `Testing reader for the Boolean type (false value)`() {
        val input: JsValue = JsBoolean.valueOf(false)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsSuccess(location = JsLocation.empty, value = false)
    }

    @Test
    fun `Testing reader for the Boolean type (reading from invalid node)`() {
        val input: JsValue = JsString("abc")

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsResult.Failure.Cause(
                location = JsLocation.empty,
                error = JsonErrors.InvalidType(expected = JsValue.Type.BOOLEAN, actual = JsValue.Type.STRING)
            )
        )
    }
}
