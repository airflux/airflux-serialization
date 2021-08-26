package io.github.airflux.reader.validator.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.readRequired
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class JsPropertyValidatorExtensionTest {

    companion object {
        private val context = JsReaderContext()
        private val isNotEmpty = JsPropertyValidator<String> { _, _, value ->
            if (value.isNotEmpty()) emptyList() else listOf(JsonErrors.Validation.Strings.IsEmpty)
        }

        val stringReader: JsReader<String> = JsReader { _, path, input ->
            when (input) {
                is JsString -> JsResult.Success(input.underlying, path)
                else -> JsResult.Failure(
                    path = path,
                    error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                )
            }
        }
    }

    @Nested
    inner class Reader {

        @Test
        fun `Testing of the extension-function the validation for JsReader`() {
            val json: JsValue = JsObject("name" to JsString("user"))
            val reader = JsReader { context, path, input ->
                val result = input.lookup(path, JsPath.Root / "name")
                readRequired(
                    from = result,
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )
            }.validation(isNotEmpty)

            val result = reader.read(context, JsResultPath.Root, json)

            result.assertAsSuccess(path = JsResultPath.Root / "name", value = "user")
        }

        @Test
        fun `Testing of the extension-function the validation for JsReader (error of validation)`() {
            val json: JsValue = JsObject("name" to JsString(""))
            val reader = JsReader { context, path, input ->
                val result = input.lookup(path, JsPath.Root / "name")
                readRequired(
                    from = result,
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )
            }.validation(isNotEmpty)

            val result = reader.read(context, JsResultPath.Root, json)

            result.assertAsFailure(JsResultPath.Root / "name" to listOf(JsonErrors.Validation.Strings.IsEmpty))
        }

        @Test
        fun `Testing of the extension-function the validation for JsReader (result is failure)`() {
            val json: JsValue = JsObject("name" to JsNull)
            val reader = JsReader { context, path, input ->
                val result = input.lookup(path, JsPath.Root / "name")
                readRequired(
                    from = result,
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )
            }.validation(isNotEmpty)

            val result = reader.read(context, JsResultPath.Root, json)

            result.assertAsFailure(
                JsResultPath.Root / "name" to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NULL)
                )
            )
        }
    }

    @Nested
    inner class Result {

        @Test
        fun `Testing of the extension-function the validation for JsResult`() {
            val result: JsResult<String> = JsResult.Success(path = JsResultPath.Root / "name", value = "user")

            val validated = result.validation(isNotEmpty)

            validated.assertAsSuccess(path = JsResultPath.Root / "name", value = "user")
        }

        @Test
        fun `Testing of the extension-function the validation for JsResult (error of validation)`() {
            val result: JsResult<String> = JsResult.Success(path = JsResultPath.Root / "user", value = "")

            val validated = result.validation(isNotEmpty)

            validated.assertAsFailure(JsResultPath.Root / "user" to listOf(JsonErrors.Validation.Strings.IsEmpty))
        }

        @Test
        fun `Testing of the extension-function the validation for JsResult (result is failure)`() {
            val result: JsResult<String> =
                JsResult.Failure(path = JsResultPath.Root / "user", error = JsonErrors.PathMissing)

            val validated = result.validation(isNotEmpty)

            validated.assertAsFailure(JsResultPath.Root / "user" to listOf(JsonErrors.PathMissing))
        }
    }
}
