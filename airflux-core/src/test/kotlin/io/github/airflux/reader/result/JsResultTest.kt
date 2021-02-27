package io.github.airflux.reader.result

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.path.JsPath
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JsResultTest {

    @Nested
    inner class Success {

        @Test
        fun `Testing 'map' function of the Success class`() {
            val originalValue = "10"
            val original: JsResult<String> = JsResult.Success(path = JsPath.empty / "id", value = originalValue)
            val result = original.map { it.toInt() }

            result.assertAsSuccess(path = JsPath.empty / "id", value = originalValue.toInt())
        }

        @Test
        fun `Testing 'flatMap' function of the Success class`() {
            val originalValue = "10"
            val original: JsResult<String> = JsResult.Success(path = JsPath.empty / "id", value = originalValue)

            val result = original.flatMap { JsResult.Success(it.toInt()) }

            result.assertAsSuccess(path = JsPath.empty, value = originalValue.toInt())
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

            result.assertAsSuccess(path = JsPath.empty / "user" / "id", value = originalValue)
        }
    }

    @Nested
    inner class Failure {

        @Nested
        inner class Constructors {

            @Test
            fun `Testing the constructor of the Failure class without parameters`() {
                val original = JsResult.Failure()

                original.assertAsFailure(
                    JsPath.empty to emptyList()
                )
            }

            @Test
            fun `Testing the constructor of the Failure class with only error description`() {

                val original = JsResult.Failure(JsonErrors.PathMissing)

                original.assertAsFailure(
                    JsPath.empty to listOf(JsonErrors.PathMissing)
                )
            }

            @Test
            fun `Testing the constructor of the Failure class with path and one error description`() {
                val original = JsResult.Failure(JsPath.empty / "user", JsonErrors.PathMissing)

                original.assertAsFailure(
                    JsPath.empty / "user" to listOf(JsonErrors.PathMissing)
                )
            }

            @Test
            fun `Testing the constructor of the Failure class with path and errors description`() {
                val original = JsResult.Failure(JsPath.empty / "user", listOf(JsonErrors.PathMissing))

                original.assertAsFailure(
                    JsPath.empty / "user" to listOf(JsonErrors.PathMissing)
                )
            }
        }

        @Test
        fun `Testing 'map' function of the Failure class`() {
            val original: JsResult<String> =
                JsResult.Failure(path = JsPath.empty / "name", error = JsonErrors.PathMissing)

            val result = original.map { it.toInt() }

            result.assertAsFailure(
                JsPath.empty / "name" to listOf(JsonErrors.PathMissing)
            )
        }

        @Test
        fun `Testing 'flatMap' function of the Failure class`() {
            val original: JsResult<String> =
                JsResult.Failure(path = JsPath.empty / "name", error = JsonErrors.PathMissing)

            val result = original.flatMap { JsResult.Success(it.toInt()) }

            result.assertAsFailure(
                JsPath.empty / "name" to listOf(JsonErrors.PathMissing)
            )
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
            val pathOfUser = JsPath.empty / "user"
            val pathOfId = JsPath.empty / "id"
            val original: JsResult<String> = JsResult.Failure()

            val result = original.repath(pathOfId).repath(pathOfUser)

            result.assertAsFailure(
                JsPath.empty / "user" / "id" to emptyList()
            )
        }
    }

    fun <T> getErrorOrNull(result: JsResult<T>): JsResult.Failure? {
        result.onFailure { return it }
        return null
    }
}
