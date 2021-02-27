package io.github.airflux.path

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.ObjectContract
import io.github.airflux.path.JsPath.Companion.div
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsPathTest {
    companion object {
        private const val KEY_PATH_ELEMENT_VALUE = "user"
        private const val IDX_PATH_ELEMENT_VALUE = 10
    }

    @Nested
    inner class CompanionObject {

        @Test
        fun `Testing 'repath' function in companion object`() {
            val pathOfUser = JsPath("user")
            val pathOfId = JsPath("id")
            val failures = listOf(Pair(pathOfId, listOf(JsonErrors.PathMissing)))

            val result = JsPath.repath(failures, pathOfUser)

            assertEquals(1, result.size)
            val failure = result[0]
            assertEquals(JsPath.empty / "user" / "id", failure.first)

            val errors = failure.second
            assertEquals(1, errors.size)
            val error = errors[0]
            assertTrue(error is JsonErrors.PathMissing)
        }
    }

    @Nested
    inner class Constructors {

        @Test
        fun `Testing the constructor of the JsPath class without parameters`() {
            val path = JsPath.empty

            assertTrue(path.elements.isEmpty())
        }

        @Test
        fun `Testing the constructor of the JsPath class with text parameter`() {
            val path = JsPath(KEY_PATH_ELEMENT_VALUE)

            assertEquals(1, path.elements.size)
            val element = path.elements[0] as KeyPathElement
            assertEquals(KEY_PATH_ELEMENT_VALUE, element.key)
        }

        @Test
        fun `Testing the constructor of the JsPath class with number parameter`() {
            val path = JsPath(IDX_PATH_ELEMENT_VALUE)

            assertEquals(1, path.elements.size)
            val element = path.elements[0] as IdxPathElement
            assertEquals(IDX_PATH_ELEMENT_VALUE, element.idx)
        }
    }

    @Test
    fun `Testing 'toString' function of the JsPath class`() {
        ObjectContract.checkToString(JsPath.empty, "#")
        ObjectContract.checkToString(JsPath.empty / "user" / "name", "#/user/name")
    }

    @Test
    fun `Testing 'equals contract' of the JsPath class`() {
        ObjectContract.checkEqualsContract(
            JsPath.empty / "user" / "name",
            JsPath.empty / "user" / "name",
            JsPath.empty / "user" / "phones"
        )
    }

    @Test
    fun `Create a path by two text elements`() {
        val path = "user" / "name"

        assertEquals(expected = 2, actual = path.elements.size)

        val firstElement = path.elements[0] as KeyPathElement
        assertEquals(expected = "user", actual = firstElement.key)

        val secondElement = path.elements[1] as KeyPathElement
        assertEquals(expected = "name", actual = secondElement.key)
    }

    @Test
    fun `Create a path by text element and index element`() {
        val path = "phones" / 0

        assertEquals(expected = 2, actual = path.elements.size)

        val firstElement = path.elements[0] as KeyPathElement
        assertEquals(expected = "phones", actual = firstElement.key)

        val secondElement = path.elements[1] as IdxPathElement
        assertEquals(expected = 0, actual = secondElement.idx)
    }
}
