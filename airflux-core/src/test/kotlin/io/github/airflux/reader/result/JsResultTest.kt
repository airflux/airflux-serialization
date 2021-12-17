package io.github.airflux.reader.result

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.reader.result.JsResult.Failure.Cause.Companion.bind
import io.github.airflux.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.value.JsValue
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JsResultTest {

    @Nested
    inner class Success {

        @Test
        fun `Testing the map function of the Success class`() {
            val originalValue = "10"
            val original: JsResult<String> = JsResult.Success(location = JsLocation.Root / "id", value = originalValue)
            val result = original.map { it.toInt() }

            result.assertAsSuccess(location = JsLocation.Root / "id", value = originalValue.toInt())
        }

        @Test
        fun `Testing the flatMap function of the Success class`() {
            val originalValue = "10"
            val original: JsResult<String> = JsResult.Success(location = JsLocation.Root / "id", value = originalValue)

            val result = original.flatMap { v, p -> JsResult.Success(v.toInt(), p) }

            result.assertAsSuccess(location = JsLocation.Root / "id", value = originalValue.toInt())
        }

        @Test
        fun `Testing the orElse function of the Success class`() {
            val originalValue = "10"
            val elseValue = "20"
            val original: JsResult<String> = JsResult.Success(location = JsLocation.Root, value = originalValue)

            val result = original.orElse { elseValue }

            assertEquals(originalValue, result)
        }

        @Test
        fun `Testing the getOrElse function of the Success class`() {
            val originalValue = "10"
            val elseValue = "20"
            val original: JsResult<String> = JsResult.Success(location = JsLocation.Root, value = originalValue)

            val result = original.getOrElse(elseValue)

            assertEquals(originalValue, result)
        }

        @Test
        fun `Testing the onFailure function of the Success class`() {
            val originalValue = "10"

            val original: JsResult<String> = JsResult.Success(location = JsLocation.Root, value = originalValue)
            val error: JsResult.Failure? = getErrorOrNull(original)

            assertNull(error)
        }
    }

    @Nested
    inner class Failure {

        @Nested
        inner class Constructors {

            @Test
            fun `Testing the constructor of the Failure class with only error description`() {

                val original = JsResult.Failure(location = JsLocation.Root, error = JsonErrors.PathMissing)

                original.assertAsFailure(JsLocation.Root bind JsonErrors.PathMissing)
            }

            @Test
            fun `Testing the constructor of the Failure class with path and one error description`() {
                val original = JsResult.Failure(location = JsLocation.Root / "user", error = JsonErrors.PathMissing)

                original.assertAsFailure("user" bind JsonErrors.PathMissing)
            }

            @Test
            fun `Testing the constructor of the Failure class with path and errors description`() {
                val original = JsResult.Failure(location = JsLocation.Root / "user", error = JsonErrors.PathMissing)

                original.assertAsFailure("user" bind JsonErrors.PathMissing)
            }
        }

        @Test
        fun `Testing the map function of the Failure class`() {
            val original: JsResult<String> =
                JsResult.Failure(location = JsLocation.Root / "name", error = JsonErrors.PathMissing)

            val result = original.map { it.toInt() }

            result.assertAsFailure("name" bind JsonErrors.PathMissing)
        }

        @Test
        fun `Testing the flatMap function of the Failure class`() {
            val original: JsResult<String> =
                JsResult.Failure(location = JsLocation.Root / "name", error = JsonErrors.PathMissing)

            val result = original.flatMap { v, p ->
                JsResult.Success(v.toInt(), p)
            }

            result.assertAsFailure("name" bind JsonErrors.PathMissing)
        }

        @Test
        fun `Testing the orElse function of the Failure class`() {
            val elseValue = "20"
            val original: JsResult<String> = JsResult.Failure(location = JsLocation.Root, error = JsonErrors.PathMissing)

            val result = original.orElse { elseValue }

            assertEquals(elseValue, result)
        }

        @Test
        fun `Testing the getOrElse function of the Failure class`() {
            val elseValue = "20"
            val original: JsResult<String> = JsResult.Failure(location = JsLocation.Root, error = JsonErrors.PathMissing)

            val result = original.getOrElse(elseValue)

            assertEquals(elseValue, result)
        }

        @Test
        fun `Testing the onFailure function of the Failure class`() {
            val original: JsResult<String> = JsResult.Failure(location = JsLocation.Root, error = JsonErrors.PathMissing)

            val error: JsResult.Failure? = getErrorOrNull(original)

            assertNotNull(error)
        }

        @Nested
        inner class Cause {

            @Test
            fun `Testing extension function the bind for JsResultPath`() {

                val cause = JsLocation.Root / "name" bind JsonErrors.PathMissing

                assertEquals(JsLocation.Root / "name", cause.location)
                assertEquals(1, cause.errors.count())
                assertContains(cause.errors, JsonErrors.PathMissing)
            }

            @Test
            fun `Testing extension function the bind for Int type`() {

                val cause = JsLocation.Root / 1 bind JsonErrors.PathMissing

                assertEquals(JsLocation.Root / 1, cause.location)
                assertEquals(1, cause.errors.count())
                assertContains(cause.errors, JsonErrors.PathMissing)
            }

            @Test
            fun `Testing extension function the bind for String type`() {

                val cause = "name" bind JsonErrors.PathMissing

                assertEquals(JsLocation.Root / "name", cause.location)
                assertEquals(1, cause.errors.count())
                assertContains(cause.errors, JsonErrors.PathMissing)
            }
        }
    }

    @Test
    fun `Testing the merge function`() {
        val failures = listOf(
            JsResult.Failure(location = JsLocation.Root / "id", error = JsonErrors.PathMissing),
            JsResult.Failure(
                location = JsLocation.Root / "name",
                error = JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING)
            )
        )

        val failure = failures.merge()

        assertContains(failure.causes, JsLocation.Root / "id" bind JsonErrors.PathMissing)
        assertContains(
            failure.causes,
            JsLocation.Root / "name" bind JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING)
        )
    }

    fun <T> getErrorOrNull(result: JsResult<T>): JsResult.Failure? {
        result.onFailure { return it }
        return null
    }
}
