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

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = value)
    }

    @Test
    fun `Testing reader for the BigDecimal type (long max value)`() {
        val value = BigDecimal.valueOf(Long.MAX_VALUE)
        val input: JsValue = JsNumber.valueOf(value.toPlainString())!!

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsSuccess(location = JsLocation.Root, value = value)
    }

    @Test
    fun `Testing reader for the BigDecimal type (reading from invalid node)`() {
        val input: JsValue = JsString("abc")

        val result = reader.read(context, JsLocation.Root, input)

        result.assertAsFailure(
            JsLocation.Root bind JsonErrors.InvalidType(
                expected = JsValue.Type.NUMBER,
                actual = JsValue.Type.STRING
            )
        )
    }
}
