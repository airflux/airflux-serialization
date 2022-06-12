package io.github.airflux.core.reader.validator.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.`object`.readRequired
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.JsValidator
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test

internal class JsValidatorExtensionTest {

    companion object {
        private val CONTEXT = JsReaderContext(
            listOf(
                PathMissingErrorBuilder { JsonErrors.PathMissing },
                InvalidTypeErrorBuilder(JsonErrors::InvalidType)
            )
        )
        private val isNotEmpty = JsValidator<String> { _, location, value ->
            if (value.isNotEmpty()) null else JsResult.Failure(location, JsonErrors.Validation.Strings.IsEmpty)
        }

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

    @Nested
    inner class Reader {

        @Test
        fun `Testing of the extension-function the validation for JsReader`() {
            val json: JsValue = JsObject("name" to JsString("user"))
            val reader = JsReader { context, location, input ->
                val result = JsLookup.apply(location, JsPath("name"), input)
                readRequired(context = context, from = result, using = stringReader)
            }.validation(isNotEmpty)

            val result = reader.read(CONTEXT, JsLocation.empty, json)

            result.assertAsSuccess(location = JsLocation.empty.append("name"), value = "user")
        }

        @Test
        fun `Testing of the extension-function the validation for JsReader (error of validation)`() {
            val json: JsValue = JsObject("name" to JsString(""))
            val reader = JsReader { context, location, input ->
                val result = JsLookup.apply(location, JsPath("name"), input)
                readRequired(context = context, from = result, using = stringReader)
            }.validation(isNotEmpty)

            val result = reader.read(CONTEXT, JsLocation.empty, json)

            result.assertAsFailure(
                JsResult.Failure.Cause(
                    location = JsLocation.empty.append("name"),
                    error = JsonErrors.Validation.Strings.IsEmpty
                )
            )
        }

        @Test
        fun `Testing of the extension-function the validation for JsReader (result is failure)`() {
            val json: JsValue = JsObject("name" to JsNull)
            val reader = JsReader { context, location, input ->
                val result = JsLookup.apply(location, JsPath("name"), input)
                readRequired(context = context, from = result, using = stringReader)
            }.validation(isNotEmpty)

            val result = reader.read(CONTEXT, JsLocation.empty, json)

            result.assertAsFailure(
                JsResult.Failure.Cause(
                    location = JsLocation.empty.append("name"),
                    error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = JsValue.Type.NULL)
                )
            )
        }
    }

    @Nested
    inner class Result {

        @Test
        fun `Testing of the extension-function the validation for JsResult`() {
            val result: JsResult<String> = JsResult.Success(location = JsLocation.empty.append("name"), value = "user")

            val validated = result.validation(isNotEmpty)

            validated.assertAsSuccess(location = JsLocation.empty.append("name"), value = "user")
        }

        @Test
        fun `Testing of the extension-function the validation for JsResult (error of validation)`() {
            val result: JsResult<String> = JsResult.Success(location = JsLocation.empty.append("user"), value = "")

            val validated = result.validation(isNotEmpty)

            validated.assertAsFailure(
                JsResult.Failure.Cause(
                    location = JsLocation.empty.append("user"),
                    error = JsonErrors.Validation.Strings.IsEmpty
                )
            )
        }

        @Test
        fun `Testing of the extension-function the validation for JsResult (result is failure)`() {
            val result: JsResult<String> =
                JsResult.Failure(location = JsLocation.empty.append("user"), error = JsonErrors.PathMissing)

            val validated = result.validation(isNotEmpty)

            validated.assertAsFailure(
                JsResult.Failure.Cause(
                    location = JsLocation.empty.append("user"),
                    error = JsonErrors.PathMissing
                )
            )
        }
    }
}
