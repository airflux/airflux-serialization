package io.github.airflux.reader.result

import io.github.airflux.common.JsonErrors
import io.github.airflux.value.JsValue
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class JsErrorsTest {

    @Test
    fun `Testing the of function for once error`() {

        val failure = JsErrors.of(JsonErrors.PathMissing)

        assertEquals(1, failure.count())
        assertContains(failure, JsonErrors.PathMissing)
    }

    @Test
    fun `Testing the of function for more errors`() {

        val errors = JsErrors.of(
            listOf(
                JsonErrors.PathMissing,
                JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING),
                JsonErrors.ValueCast("10", Boolean::class)
            )
        )

        assertNotNull(errors)
        assertEquals(3, errors.count())
        assertContains(errors, JsonErrors.PathMissing)
        assertContains(errors, JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING))
        assertContains(errors, JsonErrors.ValueCast("10", Boolean::class))
    }

    @Test
    fun `Testing the hasCritical function for non-critical errors`() {
        val errors = JsErrors.of(
            listOf(
                JsonErrors.PathMissing,
                JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING),
                JsonErrors.ValueCast("10", Boolean::class)
            )
        )

        assertNotNull(errors)
    }

    @Test
    fun `Testing the plus function`() {

        val firstFailure = JsErrors.of(JsonErrors.PathMissing)
        val secondFailure = JsErrors.of(JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING))
        val threeFailure = JsErrors.of(JsonErrors.ValueCast("10", Boolean::class))

        val merged = firstFailure + secondFailure + threeFailure

        assertContains(merged, JsonErrors.PathMissing)
        assertContains(merged, JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING))
        assertContains(merged, JsonErrors.ValueCast("10", Boolean::class))
    }

    @Test
    fun `Testing the equals function`() {
        val firstFailure = JsErrors.of(JsonErrors.PathMissing)
        val secondFailure = JsErrors.of(JsonErrors.PathMissing)
        val threeFailure = JsErrors.of(JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING))
        val fourFailure: JsErrors? = null

        assertEquals(firstFailure, firstFailure)

        assertEquals(firstFailure, secondFailure)
        assertEquals(secondFailure, firstFailure)

        assertNotEquals(firstFailure, threeFailure)
        assertNotEquals(threeFailure, firstFailure)

        assertNotEquals(firstFailure, fourFailure)
        assertNotEquals(fourFailure, firstFailure)
    }

    @Test
    fun `Testing the hashCode function`() {
        val firstFailure = JsErrors.of(JsonErrors.PathMissing)
        val secondFailure = JsErrors.of(JsonErrors.PathMissing)
        val threeFailure = JsErrors.of(JsonErrors.InvalidType(JsValue.Type.BOOLEAN, JsValue.Type.STRING))

        assertEquals(firstFailure.hashCode(), firstFailure.hashCode())

        assertEquals(firstFailure.hashCode(), secondFailure.hashCode())
        assertEquals(secondFailure.hashCode(), firstFailure.hashCode())

        assertNotEquals(firstFailure.hashCode(), threeFailure.hashCode())
        assertNotEquals(threeFailure.hashCode(), firstFailure.hashCode())
    }
}
