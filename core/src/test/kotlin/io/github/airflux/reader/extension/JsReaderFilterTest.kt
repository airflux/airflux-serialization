package io.github.airflux.reader.extension

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.path.JsPath
import io.github.airflux.reader.JsReader
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.predicate.JsPredicate
import io.github.airflux.reader.readNullable
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import io.github.airflux.value.JsValue
import io.github.airflux.value.extension.lookup
import kotlin.test.Test

class JsReaderFilterTest {

    companion object {
        private val context = JsReaderContext()
        private val isNotBlank = JsPredicate<String> { _, _, value -> value.isNotBlank() }

        private val stringReader: JsReader<String> = JsReader { _, location, input ->
            when (input) {
                is JsString -> JsResult.Success(input.get, location)
                else -> JsResult.Failure(
                    location = location,
                    error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                )
            }
        }

        private val reader = JsReader { context, location, input ->
            val result = input.lookup(location, JsPath("name"))
            readNullable(
                from = result,
                using = stringReader,
                context = context,
                pathMissingErrorBuilder = { JsonErrors.PathMissing },
                invalidTypeErrorBuilder = JsonErrors::InvalidType
            )
        }
    }

    @Test
    fun `The value does not satisfy the predicate`() {
        val json: JsValue = JsObject("name" to JsString("user"))

        val result = reader.filter(isNotBlank).read(context, JsLocation.Root, json)

        result.assertAsSuccess(location = JsLocation.Root / "name", value = "user")
    }

    @Test
    fun `The value satisfies the predicate`() {
        val json: JsValue = JsObject("name" to JsString("  "))

        val result = reader.filter(isNotBlank).read(context, JsLocation.Root, json)

        result.assertAsSuccess(location = JsLocation.Root / "name", value = null)
    }

    @Test
    fun `The value is null`() {
        val json: JsValue = JsObject("name" to JsNull)

        val result = reader.filter(isNotBlank).read(context, JsLocation.Root, json)

        result.assertAsSuccess(location = JsLocation.Root / "name", value = null)
    }

    @Test
    fun `The failure result does no filtering`() {
        val json: JsValue = JsObject("user" to JsString("  "))

        val result = reader.filter(isNotBlank).read(context, JsLocation.Root, json)

        result.assertAsFailure("name" bind JsonErrors.PathMissing)
    }
}
