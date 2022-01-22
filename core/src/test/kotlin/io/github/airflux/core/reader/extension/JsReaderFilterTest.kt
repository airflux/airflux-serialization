package io.github.airflux.core.reader.extension

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.readNullable
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import kotlin.test.Test

class JsReaderFilterTest {

    companion object {
        private val context = JsReaderContext()
        private val isNotBlank = JsPredicate<String> { _, _, value -> value.isNotBlank() }

        private val stringReader: JsReader<String> = JsReader { _, location, input ->
            when (input) {
                is JsString -> JsResult.Success(location, input.get)
                else -> JsResult.Failure(
                    location = location,
                    error = JsonErrors.InvalidType(expected = JsValue.Type.STRING, actual = input.type)
                )
            }
        }

        private val reader = JsReader { context, location, input ->
            val result = JsLookup.apply(location, JsPath("name"), input)
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

        val result = reader.filter(isNotBlank).read(context, JsLocation.empty, json)

        result.assertAsSuccess(location = JsLocation.empty.append("name"), value = "user")
    }

    @Test
    fun `The value satisfies the predicate`() {
        val json: JsValue = JsObject("name" to JsString("  "))

        val result = reader.filter(isNotBlank).read(context, JsLocation.empty, json)

        result.assertAsSuccess(location = JsLocation.empty.append("name"), value = null)
    }

    @Test
    fun `The value is null`() {
        val json: JsValue = JsObject("name" to JsNull)

        val result = reader.filter(isNotBlank).read(context, JsLocation.empty, json)

        result.assertAsSuccess(location = JsLocation.empty.append("name"), value = null)
    }

    @Test
    fun `The failure result does no filtering`() {
        val json: JsValue = JsObject("user" to JsString("  "))

        val result = reader.filter(isNotBlank).read(context, JsLocation.empty, json)

        result.assertAsFailure(
            JsResult.Failure.Cause(location = JsLocation.empty.append("name"), error = JsonErrors.PathMissing)
        )
    }
}
