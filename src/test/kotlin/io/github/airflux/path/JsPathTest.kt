package io.github.airflux.path

import io.github.airflux.common.ObjectContract
import io.github.airflux.dsl.PathDsl.div
import io.github.airflux.reader.result.JsError
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
            val failures = listOf(Pair(pathOfId, listOf(JsError.PathMissing)))

            val result = JsPath.repath(failures, pathOfUser)

            assertEquals(1, result.size)
            val failure = result[0]
            assertEquals("user" / "id", failure.first)

            val errors = failure.second
            assertEquals(1, errors.size)
            val error = errors[0]
            assertTrue(error is JsError.PathMissing)
        }
    }

    @Nested
    inner class Constructors {

        @Test
        fun `Testing the constructor of the JsPath class without parameters`() {
            val path = JsPath()

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
        ObjectContract.checkToString(JsPath(), "#")
        ObjectContract.checkToString("user" / "name", "#/user/name")
    }

    @Test
    fun `Testing 'equals contract' of the JsPath class`() {
        ObjectContract.checkEqualsContract(
            "user" / "name",
            "user" / "name",
            "user" / "phones"
        )
    }
}
