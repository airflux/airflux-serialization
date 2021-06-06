package io.github.airflux.reader.filter.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.filter.JsPredicate
import io.github.airflux.reader.readRequired
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class JsPredicateExtensionTest {

    companion object {
        private val context = JsReaderContext()
        private val isNotBlank = JsPredicate<String> { _, value -> value.isNotBlank() }

        val stringReader: JsReader<String> = JsReader { _, input ->
            when (input) {
                is JsString -> JsResult.Success(input.underlying)
                else -> JsResult.Failure(
                    error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                )
            }
        }
    }

    @Nested
    inner class Reader {

        @Test
        fun `Testing of the extension-function 'filter' for JsReader`() {
            val json: JsValue = JsObject("name" to JsString("user"))
            val reader = JsReader { context, input ->
                readRequired(
                    from = input,
                    path = JsPath.empty / "name",
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )
            }.filter(isNotBlank)

            val result = reader.read(context, json)

            result.assertAsSuccess(path = JsPath.empty / "name", value = "user")
        }

        @Test
        fun `Testing of the extension-function 'filter' for JsReader (filtered)`() {
            val json: JsValue = JsObject("name" to JsString("  "))
            val reader = JsReader { context, input ->
                readRequired(
                    from = input,
                    path = JsPath.empty / "name",
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )
            }.filter(isNotBlank)

            val result = reader.read(context, json)

            result.assertAsSuccess(path = JsPath.empty / "name", value = null)
        }

        @Test
        fun `Testing of the extension-function 'filter' for JsReader as failure`() {
            val json: JsValue = JsObject("user" to JsString("  "))
            val reader = JsReader { context, input ->
                readRequired(
                    from = input,
                    path = JsPath.empty / "name",
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )
            }.filter(isNotBlank)

            val result = reader.read(context, json)

            result.assertAsFailure(JsPath.empty / "name" to listOf(JsonErrors.PathMissing))
        }
    }

    @Nested
    inner class Result {

        @Test
        fun `Testing of the extension-function 'filter' for JsResult`() {
            val result: JsResult<String> = JsResult.Success(path = JsPath.empty / "name", value = "user")

            val validated = result.filter(isNotBlank)

            validated.assertAsSuccess(path = JsPath.empty / "name", value = "user")
        }

        @Test
        fun `Testing of the extension-function 'filter' for JsResult (filtered)`() {
            val result: JsResult<String> = JsResult.Success(path = JsPath.empty / "name", value = "  ")

            val validated = result.filter(isNotBlank)

            validated.assertAsSuccess(path = JsPath.empty / "name", value = null)
        }

        @Test
        fun `Testing of the extension-function 'filter' for JsResult as failure`() {
            val error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.BOOLEAN)
            val result: JsResult<String> = JsResult.Failure(path = JsPath.empty / "name", error = error)

            val validated = result.filter(isNotBlank)

            validated.assertAsFailure(JsPath.empty / "name" to listOf(error))
        }
    }
}
