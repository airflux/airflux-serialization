package io.github.airflux.core.reader.base

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import kotlin.test.Test

class BuilderStringReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val reader = buildStringReader(JsonErrors::InvalidType)
    }

    @Test
    fun `Testing reader for the String type`() {
        val value = "abc"
        val input: JsValue = JsString(value)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsSuccess(location = JsLocation.empty, value = value)
    }

    @Test
    fun `Testing reader for the String type (reading from invalid node)`() {
        val input: JsValue = JsBoolean.valueOf(true)

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsLocation.empty bind JsonErrors.InvalidType(
                expected = JsValue.Type.STRING,
                actual = JsValue.Type.BOOLEAN
            )
        )
    }
}
