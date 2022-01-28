package io.github.airflux.core.reader.result.extension

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import kotlin.test.Test

class JsResultFilterTest {

    companion object {
        private val isNotBlank = JsPredicate<String> { _, _, value -> value.isNotBlank() }

        val stringReader: JsReader<String> = JsReader { _, location, input ->
            if (input is JsString)
                JsResult.Success(location, input.get)
            else
                JsResult.Failure(
                    location = location,
                    error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                )
        }
    }

    @Test
    fun `The value satisfies the predicate`() {
        val result: JsResult<String> = JsResult.Success(location = JsLocation.empty.append("name"), value = "  ")

        val validated = result.filter(isNotBlank)

        validated.assertAsSuccess(location = JsLocation.empty.append("name"), value = null)
    }

    @Test
    fun `The value does not satisfy the predicate`() {
        val result: JsResult<String> = JsResult.Success(location = JsLocation.empty.append("name"), value = "user")

        val validated = result.filter(isNotBlank)

        validated.assertAsSuccess(location = JsLocation.empty.append("name"), value = "user")
    }

    @Test
    fun `The value is null`() {
        val result: JsResult<String?> = JsResult.Success(location = JsLocation.empty.append("name"), value = null)

        val validated = result.filter(isNotBlank)

        validated.assertAsSuccess(location = JsLocation.empty.append("name"), value = null)
    }

    @Test
    fun `The failure result does no filtering`() {
        val error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
        val result: JsResult<String> = JsResult.Failure(location = JsLocation.empty.append("name"), error = error)

        val validated = result.filter(isNotBlank)

        validated.assertAsFailure(JsResult.Failure.Cause(location = JsLocation.empty.append("name"), error = error))
    }
}
