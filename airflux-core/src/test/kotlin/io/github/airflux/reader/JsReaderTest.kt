package io.github.airflux.reader

import io.github.airflux.path.JsPath
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsNull
import io.github.airflux.value.JsValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsReaderTest {

    companion object {
        private val ID_PATH = JsPath("id")
        private val IDENTIFIER_PATH = JsPath("identifier")
        private const val ID_VALUE = "10"
        private const val IDENTIFIER_VALUE = "100"
    }

    @Test
    fun `Testing 'map' function of the JsReader class`() {
        val reader = JsReader {
            JsResult.Success(path = ID_PATH, value = ID_VALUE)
        }
        val transformedReader = reader.map { value -> value.toInt() }

        val result = transformedReader.read(JsNull)

        result as JsResult.Success
        assertEquals(ID_PATH, result.path)
        assertEquals(ID_VALUE.toInt(), result.value)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (first reader)`() {
        val idReader = JsReader {
            JsResult.Success(path = ID_PATH, value = ID_VALUE)
        }
        val identifierReader = JsReader<String> {
            JsResult.Failure(path = IDENTIFIER_PATH, error = JsError.PathMissing)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(JsNull)

        result as JsResult.Success
        assertEquals(ID_PATH, result.path)
        assertEquals(ID_VALUE, result.value)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (second reader)`() {
        val idReader = JsReader<String> {
            JsResult.Failure(path = ID_PATH, error = JsError.PathMissing)
        }
        val identifierReader = JsReader {
            JsResult.Success(path = IDENTIFIER_PATH, value = IDENTIFIER_VALUE)
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(JsNull)

        result as JsResult.Success
        assertEquals(IDENTIFIER_PATH, result.path)
        assertEquals(IDENTIFIER_VALUE, result.value)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (failure both reader)`() {
        val idReader = JsReader {
            JsResult.Failure(path = ID_PATH, error = JsError.PathMissing)
        }
        val identifierReader = JsReader {
            JsResult.Failure(
                path = IDENTIFIER_PATH,
                error = JsError.InvalidType(expected = JsValue.Type.OBJECT, actual = JsValue.Type.STRING)
            )
        }
        val composeReader = idReader or identifierReader

        val result = composeReader.read(JsNull)

        result as JsResult.Failure
        val failures = result.errors
        assertEquals(2, failures.size)

        failures[0].also { (pathError, errors) ->
            assertEquals(ID_PATH, pathError)

            assertEquals(1, errors.size)
            assertTrue(errors[0] is JsError.PathMissing)
        }

        failures[1].also { (pathError, errors) ->
            assertEquals(IDENTIFIER_PATH, pathError)

            assertEquals(1, errors.size)
            val error = errors[0] as JsError.InvalidType
            assertEquals(JsValue.Type.OBJECT, error.expected)
            assertEquals(JsValue.Type.STRING, error.actual)
        }
    }
}
