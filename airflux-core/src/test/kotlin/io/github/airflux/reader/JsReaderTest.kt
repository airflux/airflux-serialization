package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.path.JsPath
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsValue
import kotlin.test.Test

class JsReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val ID_PATH = JsPath("id")
        private val IDENTIFIER_PATH = JsPath("identifier")
        private const val ID_VALUE = "10"
        private const val IDENTIFIER_VALUE = "100"
    }

    @Test
    fun `Testing 'map' function of the JsReader class`() {
        val reader = JsReader { _, _ ->
            JsResult.Success(path = ID_PATH, value = ID_VALUE)
        }
        val transformedReader = reader.map { value -> value.toInt() }

        val result = transformedReader.read(context, JsNull)

        result.assertAsSuccess(path = ID_PATH, value = ID_VALUE.toInt())
    }

    @Test
    fun `Testing 'or' function of the JsReader class (first reader)`() {
        val idReader = JsReader { _, _ ->
            JsResult.Success(path = ID_PATH, value = ID_VALUE)
        }
        val identifierReader = JsReader<String> { _, _ ->
            JsResult.Failure(path = IDENTIFIER_PATH, error = JsonErrors.PathMissing)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, JsNull)

        result.assertAsSuccess(path = ID_PATH, value = ID_VALUE)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (second reader)`() {
        val idReader = JsReader<String> { _, _ ->
            JsResult.Failure(path = ID_PATH, error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, _ ->
            JsResult.Success(path = IDENTIFIER_PATH, value = IDENTIFIER_VALUE)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, JsNull)

        result.assertAsSuccess(path = IDENTIFIER_PATH, value = IDENTIFIER_VALUE)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (failure both reader)`() {
        val idReader = JsReader { _, _ ->
            JsResult.Failure(path = ID_PATH, error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, _ ->
            JsResult.Failure(
                path = IDENTIFIER_PATH,
                error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, JsNull)

        result.assertAsFailure(
            ID_PATH to listOf(JsonErrors.PathMissing),
            IDENTIFIER_PATH to listOf(
                JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        )
    }
}
