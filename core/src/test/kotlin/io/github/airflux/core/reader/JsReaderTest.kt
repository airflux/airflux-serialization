package io.github.airflux.core.reader

import io.github.airflux.core.common.JsonErrors
import io.github.airflux.core.common.assertAsFailure
import io.github.airflux.core.common.assertAsSuccess
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNull
import io.github.airflux.core.value.JsValue
import kotlin.test.Test

class JsReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val location = JsLocation.empty.append("user")
        private const val ID_VALUE = "10"
        private const val IDENTIFIER_VALUE = "100"
    }

    @Test
    fun `Testing the map function of the JsReader class`() {
        val reader = JsReader { _, location, _ ->
            JsResult.Success(location = location.append("id"), value = ID_VALUE)
        }
        val transformedReader = reader.map { value -> value.toInt() }

        val result = transformedReader.read(context, location, JsNull)

        result.assertAsSuccess(location = location.append("id"), value = ID_VALUE.toInt())
    }

    @Test
    fun `Testing the or function of the JsReader class (first reader)`() {
        val idReader = JsReader { _, location, _ ->
            JsResult.Success(location = location.append("id"), value = ID_VALUE)
        }
        val identifierReader = JsReader<String> { _, location, _ ->
            JsResult.Failure(location = location.append("identifier"), error = JsonErrors.PathMissing)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, location, JsNull)

        result.assertAsSuccess(location = location.append("id"), value = ID_VALUE)
    }

    @Test
    fun `Testing the or function of the JsReader class (second reader)`() {
        val idReader = JsReader<String> { _, location, _ ->
            JsResult.Failure(location = location.append("id"), error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, location, _ ->
            JsResult.Success(location = location.append("identifier"), value = IDENTIFIER_VALUE)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, location, JsNull)

        result.assertAsSuccess(location = location.append("identifier"), value = IDENTIFIER_VALUE)
    }

    @Test
    fun `Testing the or function of the JsReader class (failure both reader)`() {
        val idReader = JsReader { _, location, _ ->
            JsResult.Failure(location = location.append("id"), error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, location, _ ->
            JsResult.Failure(
                location = location.append("identifier"),
                error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, location, JsNull)

        result.assertAsFailure(
            JsResult.Failure.Cause(location = location.append("id"), error = JsonErrors.PathMissing),
            JsResult.Failure.Cause(
                location = location.append("identifier"),
                error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        )
    }
}
