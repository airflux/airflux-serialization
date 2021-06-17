package io.github.airflux.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsValue
import kotlin.test.Test

class JsReaderTest {

    companion object {
        private val context = JsReaderContext()
        private val currentPath = JsResultPath.Root / "user"
        private const val ID_VALUE = "10"
        private const val IDENTIFIER_VALUE = "100"
    }

    @Test
    fun `Testing 'map' function of the JsReader class`() {
        val reader = JsReader { _, path, _ ->
            JsResult.Success(path = path / "id", value = ID_VALUE)
        }
        val transformedReader = reader.map { value -> value.toInt() }

        val result = transformedReader.read(context, currentPath, JsNull)

        result.assertAsSuccess(path = currentPath / "id", value = ID_VALUE.toInt())
    }

    @Test
    fun `Testing 'or' function of the JsReader class (first reader)`() {
        val idReader = JsReader { _, path, _ ->
            JsResult.Success(path = path / "id", value = ID_VALUE)
        }
        val identifierReader = JsReader<String> { _, path, _ ->
            JsResult.Failure(path = path / "identifier", error = JsonErrors.PathMissing)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, currentPath, JsNull)

        result.assertAsSuccess(path = currentPath / "id", value = ID_VALUE)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (second reader)`() {
        val idReader = JsReader<String> { _, path, _ ->
            JsResult.Failure(path = path / "id", error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, path, _ ->
            JsResult.Success(path = path / "identifier", value = IDENTIFIER_VALUE)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, currentPath, JsNull)

        result.assertAsSuccess(path = currentPath / "identifier", value = IDENTIFIER_VALUE)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (failure both reader)`() {
        val idReader = JsReader { _, path, _ ->
            JsResult.Failure(path = path / "id", error = JsonErrors.PathMissing)
        }
        val identifierReader = JsReader { _, path, _ ->
            JsResult.Failure(
                path = path / "identifier",
                error = JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(context, currentPath, JsNull)

        result.assertAsFailure(
            currentPath / "id" to listOf(JsonErrors.PathMissing),
            currentPath / "identifier" to listOf(
                JsonErrors.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        )
    }
}
