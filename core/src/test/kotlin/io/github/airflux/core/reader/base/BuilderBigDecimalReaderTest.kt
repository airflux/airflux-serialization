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
import java.math.BigDecimal
import kotlin.test.Test

class BuilderBigDecimalReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val reader = buildBigDecimalReader(JsonErrors::InvalidType)
    }

    @Test
    fun `Testing reader for the BigDecimal type (long min value)`() {
        val value = BigDecimal.valueOf(Long.MIN_VALUE)
        val input: JsValue = JsNumber.valueOf(value.toPlainString())!!

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsSuccess(location = JsLocation.empty, value = value)
    }

    @Test
    fun `Testing reader for the BigDecimal type (long max value)`() {
        val value = BigDecimal.valueOf(Long.MAX_VALUE)
        val input: JsValue = JsNumber.valueOf(value.toPlainString())!!

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsSuccess(location = JsLocation.empty, value = value)
    }

    @Test
    fun `Testing reader for the BigDecimal type (reading from invalid node)`() {
        val input: JsValue = JsString("abc")

        val result = reader.read(context, JsLocation.empty, input)

        result.assertAsFailure(
            JsLocation.empty bind JsonErrors.InvalidType(
                expected = JsValue.Type.NUMBER,
                actual = JsValue.Type.STRING
            )
        )
    }
}
