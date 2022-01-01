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

class BuilderStringReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val reader = buildStringReader(JsonErrors::InvalidType)
    }

    @Test
    fun `Testing reader for the String type`() {
        val value = "abc"
        val input: JsValue = JsString(value)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = value)
    }

    @Test
    fun `Testing reader for the String type (reading from invalid node)`() {
        val input: JsValue = JsBoolean.valueOf(true)

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.InvalidType(
                expected = JsValue.Type.STRING,
                actual = JsValue.Type.BOOLEAN
            )
        )
    }
}
