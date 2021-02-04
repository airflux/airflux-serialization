package io.github.airflux.reader

import io.github.airflux.path.JsPath
import io.github.airflux.reader.extension.readAsString
import io.github.airflux.reader.result.JsError
import io.github.airflux.reader.result.JsResult
import io.github.airflux.value.JsObject
import io.github.airflux.value.JsString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsReaderTest {

    companion object {
        const val ATTRIBUTE_NAME_ID = "id"
        const val ATTRIBUTE_NAME_IDENTIFIER = "identifier"
        const val ID_VALUE = "10"
        const val IDENTIFIER_VALUE = "100"

        val stringReader = JsReader { input -> input.readAsString() }
    }

    @Test
    fun `Testing 'map' function of the JsReader class`() {
        val json = JsObject(ATTRIBUTE_NAME_ID to JsString(ID_VALUE))
        val transformedReader = JsReader.required(JsPath(ATTRIBUTE_NAME_ID), stringReader)
            .map { value -> value.toInt() }

        val result = transformedReader.read(json)

        result as JsResult.Success
        assertEquals(ID_VALUE.toInt(), result.value)
        assertEquals(JsPath.root / ATTRIBUTE_NAME_ID, result.path)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (first reader)`() {
        val json = JsObject(ATTRIBUTE_NAME_ID to JsString(ID_VALUE))
        val idReader = JsReader.required(JsPath(ATTRIBUTE_NAME_ID), stringReader)
        val identifierReader = JsReader.required(JsPath(ATTRIBUTE_NAME_IDENTIFIER), stringReader)
        val composeReader = idReader or identifierReader

        val result = composeReader.read(json)

        result as JsResult.Success
        assertEquals(ID_VALUE, result.value)
        assertEquals(JsPath.root / ATTRIBUTE_NAME_ID, result.path)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (second reader)`() {
        val json = JsObject(ATTRIBUTE_NAME_IDENTIFIER to JsString(IDENTIFIER_VALUE))
        val idReader = JsReader.required(JsPath(ATTRIBUTE_NAME_ID), stringReader)
        val identifierReader = JsReader.required(JsPath(ATTRIBUTE_NAME_IDENTIFIER), stringReader)
        val composeReader = idReader or identifierReader

        val result = composeReader.read(json)

        result as JsResult.Success
        assertEquals(IDENTIFIER_VALUE, result.value)
        assertEquals(JsPath.root / ATTRIBUTE_NAME_IDENTIFIER, result.path)
    }

    @Test
    fun `Testing 'or' function of the JsReader class (failure both reader)`() {
        val json = JsObject()
        val idReader = JsReader.required(JsPath(ATTRIBUTE_NAME_ID), stringReader)
        val identifierReader = JsReader.required(JsPath(ATTRIBUTE_NAME_IDENTIFIER), stringReader)
        val composeReader = idReader or identifierReader

        val result = composeReader.read(json)

        result as JsResult.Failure
        val failures = result.errors
        assertEquals(2, failures.size)

        val idFailure = failures[0]
        assertEquals(JsPath(ATTRIBUTE_NAME_ID), idFailure.first)
        val idErrors = idFailure.second
        assertTrue(idErrors[0] is JsError.PathMissing)

        val identifierFailure = failures[1]
        assertEquals(JsPath(ATTRIBUTE_NAME_IDENTIFIER), identifierFailure.first)
        val identifierErrors = identifierFailure.second
        assertTrue(identifierErrors[0] is JsError.PathMissing)
    }
}
