package io.github.airflux.reader.validator.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.readRequired
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.JsValidationResult
import io.github.airflux.reader.validator.JsValidator
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class JsValidatorExtensionTest {

    companion object {
        private val context = JsReaderContext()
        private val isNotEmpty = JsValidator<String, JsonErrors.Validation> { value, _ ->
            if (value.isNotEmpty())
                JsValidationResult.Success
            else
                JsValidationResult.Failure(JsonErrors.Validation.Strings.IsEmpty)
        }

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
        fun `Testing of the extension-function 'validation' for JsReader`() {
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
            }.validation(isNotEmpty)

            val result = reader.read(context, json)

            result.assertAsSuccess(path = JsPath.empty / "name", value = "user")
        }

        @Test
        fun `Testing of the extension-function 'validation' for JsReader (error of validation)`() {
            val json: JsValue = JsObject("name" to JsString(""))
            val reader = JsReader { context, input ->
                readRequired(
                    from = input,
                    path = JsPath.empty / "name",
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )
            }.validation(isNotEmpty)

            val result = reader.read(context, json)

            result.assertAsFailure(
                JsPath.empty / "name" to listOf(JsonErrors.Validation.Strings.IsEmpty)
            )
        }

        @Test
        fun `Testing of the extension-function 'validation' for JsReader (result is failure)`() {
            val json: JsValue = JsObject("name" to JsNull)
            val reader = JsReader { context, input ->
                readRequired(
                    from = input,
                    path = JsPath.empty / "name",
                    using = stringReader,
                    context = context,
                    pathMissingErrorBuilder = { JsonErrors.PathMissing },
                    invalidTypeErrorBuilder = JsonErrors::InvalidType
                )
            }.validation(isNotEmpty)

            val result = reader.read(context, json)

            result.assertAsFailure(
                JsPath.empty / "name" to listOf(
                    JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NULL)
                )
            )
        }
    }

    @Nested
    inner class Result {

        @Test
        fun `Testing of the extension-function 'validation' for JsResult`() {
            val result: JsResult<String> = JsResult.Success(path = JsPath.empty / "name", value = "user")

            val validated = result.validation(isNotEmpty)

            validated.assertAsSuccess(path = JsPath.empty / "name", value = "user")
        }

        @Test
        fun `Testing of the extension-function 'validation' for JsResult (error of validation)`() {
            val result: JsResult<String> = JsResult.Success(path = JsPath.empty / "user", value = "")

            val validated = result.validation(isNotEmpty)

            validated.assertAsFailure(
                JsPath.empty / "user" to listOf(JsonErrors.Validation.Strings.IsEmpty)
            )
        }

        @Test
        fun `Testing of the extension-function 'validation' for JsResult (result is failure)`() {
            val result: JsResult<String> =
                JsResult.Failure(path = JsPath.empty / "user", error = JsonErrors.PathMissing)

            val validated = result.validation(isNotEmpty)

            validated.assertAsFailure(
                JsPath.empty / "user" to listOf(JsonErrors.PathMissing)
            )
        }
    }
}
