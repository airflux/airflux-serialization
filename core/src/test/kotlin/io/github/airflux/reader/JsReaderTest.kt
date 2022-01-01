package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsValue
import kotlin.test.Test

class JsReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val location = JsLocation.Root / "user"
        private const val ID_VALUE = "10"
        private const val IDENTIFIER_VALUE = "100"
    }

    @Test
    fun `Testing the map function of the JsReader class`() {
        val reader = JsReader { _, location, _ ->
            JsResult.Success(location = location / "id", value = ID_VALUE)
        }
        val transformedReader = reader.map { value -> value.toInt() }

        val result = transformedReader.read(context, location, JsNull)

        result.assertAsSuccess(location = location / "id", value = ID_VALUE.toInt())
    }

    @Test
    fun `Testing the or function of the JsReader class (first reader)`() {
        val idReader = JsReader { _, location, _ ->
            JsResult.Success(location = location / "id", value = ID_VALUE)
        }
        val identifierReader = JsReader<String> { _, location, _ ->
            JsResult.Failure(location = location / "identifier", error = JsonErrors.PathMissing)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, location, JsNull)

        result.assertAsSuccess(location = location / "id", value = ID_VALUE)
    }

    @Test
    fun `Testing the or function of the JsReader class (second reader)`() {
        val idReader = JsReader<String> { _, location, _ ->
            JsResult.Failure(location = location / "id", error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, location, _ ->
            JsResult.Success(location = location / "identifier", value = IDENTIFIER_VALUE)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, location, JsNull)

        result.assertAsSuccess(location = location / "identifier", value = IDENTIFIER_VALUE)
    }

    @Test
    fun `Testing the or function of the JsReader class (failure both reader)`() {
        val idReader = JsReader { _, location, _ ->
            JsResult.Failure(location = location / "id", error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, location, _ ->
            JsResult.Failure(
                location = location / "identifier",
                error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, location, JsNull)

        result.assertAsFailure(
            location / "id" bind JsonErrors.PathMissing,
            location / "identifier" bind JsonErrors.InvalidType(
                expected = JsValue.Type.OBJECT,
                actual = JsValue.Type.STRING
            )
        )
    }
}
