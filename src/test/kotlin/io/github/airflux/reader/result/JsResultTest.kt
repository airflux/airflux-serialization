package io.github.airflux.reader.result

import io.github.airflux.dsl.PathDsl.div
import io.github.airflux.path.JsPath
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsResultTest {

    @Nested
    inner class Success {

        @Test
        fun `Testing 'map' function of the Success class`() {
            val path = JsPath("id")
            val originalValue = "10"
            val original: JsResult<String> = JsResult.Success(path = path, value = originalValue)
            val result = original.map { it.toInt() }

            result as JsResult.Success
            assertEquals(path, result.path)
            assertEquals(originalValue.toInt(), result.value)
        }

        @Test
        fun `Testing 'flatMap' function of the Success class`() {
            val path = JsPath("id")
            val originalValue = "10"
            val original: JsResult<String> = JsResult.Success(path = path, value = originalValue)

            val result = original.flatMap { JsResult.Success(it.toInt()) }

            result as JsResult.Success
            assertEquals(JsPath(), result.path)
            assertEquals(originalValue.toInt(), result.value)
        }

        @Test
        fun `Testing 'orElse' function of the Success class`() {
            val originalValue = "10"
            val elseValue = "20"
            val original: JsResult<String> = JsResult.Success(originalValue)

            val result = original.orElse { elseValue }

            assertEquals(originalValue, result)
        }

        @Test
        fun `Testing 'getOrElse' function of the Success class`() {
            val originalValue = "10"
            val elseValue = "20"
            val original: JsResult<String> = JsResult.Success(originalValue)

            val result = original.getOrElse(elseValue)

            assertEquals(originalValue, result)
        }

        @Test
        fun `Testing 'onFailure' function of the Success class`() {
            val originalValue = "10"

            val original: JsResult<String> = JsResult.Success(originalValue)
            val error: JsResult.Failure? = getErrorOrNull(original)

            assertNull(error)
        }

        @Test
        fun `Testing 'repath' function of the Success class`() {
            val pathOfId = JsPath("id")
            val pathOfUser = JsPath("user")
            val originalValue = "10"
            val original: JsResult<String> = JsResult.Success(originalValue)

            val result = original.repath(pathOfId).repath(pathOfUser)

            result as JsResult.Success
            assertEquals(originalValue, result.value)
            assertEquals("user" / "id", result.path)
        }
    }

    @Nested
    inner class Failure {

        @Nested
        inner class Constructors {

            @Test
            fun `Testing the constructor of the Failure class without parameters`() {
                val original = JsResult.Failure()

                assertEquals(1, original.errors.size)
                val (path, errors) = original.errors[0]
                assertEquals(JsPath.root, path)
                assertTrue(errors.isEmpty())
            }

            @Test
            fun `Testing the constructor of the Failure class with only error description`() {

                val original = JsResult.Failure(JsError.PathMissing)

                assertEquals(1, original.errors.size)
                val (path, errors) = original.errors[0]
                assertEquals(JsPath.root, path)
                assertEquals(1, errors.size)
                val error = errors[0]
                assertTrue(error is JsError.PathMissing)
            }

            @Test
            fun `Testing the constructor of the Failure class with path and one error description`() {
                val path = JsPath.root / "user"

                val original = JsResult.Failure(path, JsError.PathMissing)

                assertEquals(1, original.errors.size)
                val (pathError, errors) = original.errors[0]
                assertEquals(path, pathError)
                assertEquals(1, errors.size)
                val error = errors[0]
                assertTrue(error is JsError.PathMissing)
            }

            @Test
            fun `Testing the constructor of the Failure class with path and errors description`() {
                val path = JsPath.root / "user"

                val original = JsResult.Failure(path, listOf(JsError.PathMissing))

                assertEquals(1, original.errors.size)
                val (pathError, errors) = original.errors[0]
                assertEquals(path, pathError)
                assertEquals(1, errors.size)
                assertTrue(errors[0] is JsError.PathMissing)
            }
        }

        @Test
        fun `Testing 'map' function of the Failure class`() {
            val path = JsPath("name")
            val original: JsResult<String> = JsResult.Failure(path = path, error = JsError.PathMissing)

            val result = original.map { it.toInt() }

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            val (pathError, errors) = result.errors[0]
            assertEquals(path, pathError)
            assertEquals(1, errors.size)
            assertTrue(errors[0] is JsError.PathMissing)
        }

        @Test
        fun `Testing 'flatMap' function of the Failure class`() {
            val path = JsPath("name")
            val original: JsResult<String> = JsResult.Failure(path = path, error = JsError.PathMissing)

            val result = original.flatMap { JsResult.Success(it.toInt()) }

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            val (pathError, errors) = result.errors[0]
            assertEquals(path, pathError)
            assertEquals(1, errors.size)
            assertTrue(errors[0] is JsError.PathMissing)
        }

        @Test
        fun `Testing 'orElse' function of the Failure class`() {
            val elseValue = "20"
            val original: JsResult<String> = JsResult.Failure()

            val result = original.orElse { elseValue }

            assertEquals(elseValue, result)
        }

        @Test
        fun `Testing 'getOrElse' function of the Failure class`() {
            val elseValue = "20"
            val original: JsResult<String> = JsResult.Failure()

            val result = original.getOrElse(elseValue)

            assertEquals(elseValue, result)
        }

        @Test
        fun `Testing 'onFailure' function of the Failure class`() {
            val original: JsResult<String> = JsResult.Failure()

            val error: JsResult.Failure? = getErrorOrNull(original)

            assertNotNull(error)
        }

        @Test
        fun `Testing 'repath' function of the Failure class`() {
            val pathOfUser = JsPath("user")
            val pathOfId = JsPath("id")
            val original: JsResult<String> = JsResult.Failure()

            val result = original.repath(pathOfId).repath(pathOfUser)

            result as JsResult.Failure
            assertEquals(1, result.errors.size)
            val (path, errors) = result.errors[0]
            assertEquals("user" / "id", path)
            assertTrue(errors.isEmpty())
        }
    }

    fun <T> getErrorOrNull(result: JsResult<T>): JsResult.Failure? {
        result.onFailure { return it }
        return null
    }
}
